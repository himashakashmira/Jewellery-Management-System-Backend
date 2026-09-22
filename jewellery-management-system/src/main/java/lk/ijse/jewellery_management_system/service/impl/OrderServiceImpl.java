package lk.ijse.jewellery_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.jewellery_management_system.dto.OrderDTO;
import lk.ijse.jewellery_management_system.dto.OrderDetailDTO;
import lk.ijse.jewellery_management_system.dto.OrderItemResponseDTO;
import lk.ijse.jewellery_management_system.dto.OrderResponseDTO;
import lk.ijse.jewellery_management_system.dto.OrderStatsDTO;
import lk.ijse.jewellery_management_system.dto.ReportSummaryDTO;
import lk.ijse.jewellery_management_system.entity.*;
import lk.ijse.jewellery_management_system.repository.*;
import lk.ijse.jewellery_management_system.service.GoldRateService;
import lk.ijse.jewellery_management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StockRepository stockRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final GoldRateService goldRateService;

    @Override
    public String placeOrder(OrderDTO dto) {
        // Resolve customer or fallback to guest / walk-in patron
        Customer customer;
        if (dto.getCustomerId() == null || dto.getCustomerId() <= 0) {
            String clientName = (dto.getCustomerName() != null && !dto.getCustomerName().isBlank())
                    ? dto.getCustomerName() : "Walk-in Boutique Client";
            String clientContact = (dto.getCustomerContact() != null && !dto.getCustomerContact().isBlank())
                    ? dto.getCustomerContact() : "+94 11 234 5678";

            customer = customerRepository.findAll().stream().findFirst().orElse(null);
            if (customer == null) {
                customer = customerRepository.save(Customer.builder()
                        .name(clientName)
                        .contact(clientContact)
                        .loyaltyPoints(0)
                        .build());
            }
        } else {
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElse(null);
            if (customer == null) {
                customer = customerRepository.findAll().stream().findFirst().orElse(null);
            }
        }

        String determinedOrderType = (dto.getOrderType() != null && !dto.getOrderType().isBlank())
                ? dto.getOrderType() : "POS";

        // Determine initial status: Public imitation orders default to PENDING_APPROVAL
        String initialStatus = (dto.getStatus() != null && !dto.getStatus().isBlank())
                ? dto.getStatus()
                : ("IMITATION".equalsIgnoreCase(determinedOrderType) ? "PENDING_APPROVAL" : "APPROVED");

        String orderRef = (dto.getOrderRef() != null && !dto.getOrderRef().isBlank())
                ? dto.getOrderRef()
                : "AUR-" + LocalDateTime.now().getYear() + "-" + (int)(10000 + Math.random() * 90000);

        String custName = (dto.getCustomerName() != null && !dto.getCustomerName().isBlank())
                ? dto.getCustomerName()
                : (customer != null ? customer.getName() : "Valued Patron");

        String custContact = (dto.getCustomerContact() != null && !dto.getCustomerContact().isBlank())
                ? dto.getCustomerContact()
                : (customer != null ? customer.getContact() : "");

        // Create the main Order
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .customer(customer)
                .customerName(custName)
                .customerContact(custContact)
                .orderType(determinedOrderType)
                .orderRef(orderRef)
                .deliveryAddress(dto.getDeliveryAddress())
                .paymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "Cash on Delivery (COD)")
                .status(initialStatus)
                .discount(dto.getDiscount() != null ? dto.getDiscount() : 0.0)
                .totalAmount(0.0)
                .build();

        Order savedOrder = orderRepository.save(order);
        Double finalBillAmount = 0.0;

        // Process each item in the order
        if (dto.getItems() != null) {
            for (OrderDetailDTO itemDto : dto.getItems()) {
                Product product = null;
                if (itemDto.getProductId() != null) {
                    product = productRepository.findById(itemDto.getProductId()).orElse(null);
                }

                // If product is not in database, auto-register it (e.g. curated public imitation catalog item)
                if (product == null) {
                    String pName = (itemDto.getProductName() != null && !itemDto.getProductName().isBlank())
                            ? itemDto.getProductName() : "Imitation Lifestyle Creation";
                    Double pPrice = (itemDto.getPrice() != null && itemDto.getPrice() > 0) ? itemDto.getPrice() : 14500.0;
                    String pMaterial = (itemDto.getMaterial() != null && !itemDto.getMaterial().isBlank())
                            ? itemDto.getMaterial() : "18K PVD Anti-Tarnish";

                    product = productRepository.save(Product.builder()
                            .name(pName)
                            .itemType("IMITATION")
                            .price(pPrice)
                            .material(pMaterial)
                            .build());
                }

                // Get price
                Double currentPrice;
                if (itemDto.getPrice() != null && itemDto.getPrice() > 0) {
                    currentPrice = itemDto.getPrice();
                } else {
                    currentPrice = goldRateService.calculateProductPrice(product.getId());
                }

                int qty = (itemDto.getQty() != null && itemDto.getQty() > 0) ? itemDto.getQty() : 1;

                // Save Order Details
                OrderDetail detail = OrderDetail.builder()
                        .order(savedOrder)
                        .product(product)
                        .qty(qty)
                        .unitPrice(currentPrice)
                        .build();
                orderDetailRepository.save(detail);

                // Update stock (Reduce Qty in inventory)
                Stock stock = stockRepository.findByProduct(product);
                if (stock != null) {
                    stock.setQuantity(Math.max(0, stock.getQuantity() - qty));
                    stock.setLastUpdated(LocalDateTime.now());
                    stockRepository.save(stock);
                } else {
                    Stock newStock = Stock.builder()
                            .product(product)
                            .quantity(0)
                            .lastUpdated(LocalDateTime.now())
                            .build();
                    stockRepository.save(newStock);
                }

                // If gold item is sold, update status to "SOLD" in database so it is removed from active UI
                if (!"IMITATION".equalsIgnoreCase(product.getItemType()) || "GOLD".equalsIgnoreCase(product.getItemType())) {
                    product.setStatus("SOLD");
                    productRepository.save(product);
                }

                finalBillAmount += (currentPrice * qty);
            }
        }

        // Update the final amount in the main order
        double discount = dto.getDiscount() != null ? dto.getDiscount() : 0.0;
        savedOrder.setTotalAmount(Math.max(0.0, finalBillAmount - discount));
        orderRepository.save(savedOrder);

        // Add loyalty points if customer is linked
        if (customer != null) {
            int points = (int) (finalBillAmount / 1000);
            customer.setLoyaltyPoints((customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0) + points);
            customerRepository.save(customer);
        }

        return "Order " + savedOrder.getOrderRef() + " Placed Successfully with status " + initialStatus + "!";
    }

    @Override
    public String approveOrder(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        order.setStatus("APPROVED");
        orderRepository.save(order);

        return "Order #" + (order.getOrderRef() != null ? order.getOrderRef() : order.getId()) + " Approved Successfully!";
    }

    @Override
    public List<OrderResponseDTO> getImitationOrders() {
        return orderRepository.findAll().stream()
                .filter(o -> "IMITATION".equalsIgnoreCase(o.getOrderType()) ||
                        (o.getOrderDetails() != null && o.getOrderDetails().stream()
                                .anyMatch(d -> d.getProduct() != null && "IMITATION".equalsIgnoreCase(d.getProduct().getItemType()))))
                .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()))
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderStatsDTO getOrderStats() {
        List<Order> allOrders = orderRepository.findAll();
        long orderCount = allOrders.size();
        double totalSales = allOrders.stream()
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                .sum();
        return new OrderStatsDTO(orderCount, totalSales);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()))
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToOrderResponse(order);
    }

    @Override
    public ReportSummaryDTO getReportSummary() {
        List<Order> orders = orderRepository.findAll();
        long totalOrders = orders.size();
        double grossRevenue = orders.stream()
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                .sum();
        double aov = totalOrders > 0 ? (grossRevenue / totalOrders) : 0.0;

        List<OrderDetail> allDetails = orderDetailRepository.findAll();
        long totalItemsSold = allDetails.stream()
                .mapToLong(d -> d.getQty() != null ? d.getQty() : 0)
                .sum();

        double goldRevenue = allDetails.stream()
                .filter(d -> d.getProduct() != null && !"IMITATION".equalsIgnoreCase(d.getProduct().getItemType()))
                .mapToDouble(d -> (d.getUnitPrice() != null ? d.getUnitPrice() : 0.0) * (d.getQty() != null ? d.getQty() : 1))
                .sum();

        double imitationRevenue = allDetails.stream()
                .filter(d -> d.getProduct() != null && "IMITATION".equalsIgnoreCase(d.getProduct().getItemType()))
                .mapToDouble(d -> (d.getUnitPrice() != null ? d.getUnitPrice() : 0.0) * (d.getQty() != null ? d.getQty() : 1))
                .sum();

        // Calculate imitation order counts and approval states
        List<Order> imitationOrders = orders.stream()
                .filter(o -> "IMITATION".equalsIgnoreCase(o.getOrderType()) ||
                        (o.getOrderDetails() != null && o.getOrderDetails().stream()
                                .anyMatch(d -> d.getProduct() != null && "IMITATION".equalsIgnoreCase(d.getProduct().getItemType()))))
                .collect(Collectors.toList());

        long totalImitationOrders = imitationOrders.size();
        long pendingImitationOrders = imitationOrders.stream()
                .filter(o -> "PENDING_APPROVAL".equalsIgnoreCase(o.getStatus()))
                .count();
        long approvedImitationOrders = totalImitationOrders - pendingImitationOrders;

        List<OrderResponseDTO> recent = orders.stream()
                .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()))
                .limit(10)
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());

        return ReportSummaryDTO.builder()
                .grossRevenue(Math.round(grossRevenue * 100.0) / 100.0)
                .totalOrders(totalOrders)
                .averageOrderValue(Math.round(aov * 100.0) / 100.0)
                .totalItemsSold(totalItemsSold)
                .goldRevenue(Math.round(goldRevenue * 100.0) / 100.0)
                .imitationRevenue(Math.round(imitationRevenue * 100.0) / 100.0)
                .totalImitationOrders(totalImitationOrders)
                .pendingImitationOrders(pendingImitationOrders)
                .approvedImitationOrders(approvedImitationOrders)
                .recentOrders(recent)
                .build();
    }

    private OrderResponseDTO mapToOrderResponse(Order o) {
        Customer c = o.getCustomer();
        List<OrderDetail> details = orderDetailRepository.findAll().stream()
                .filter(d -> d.getOrder() != null && d.getOrder().getId().equals(o.getId()))
                .collect(Collectors.toList());

        int totalItems = 0;
        java.util.List<lk.ijse.jewellery_management_system.dto.OrderItemResponseDTO> itemDTOs = new java.util.ArrayList<>();
        for (OrderDetail d : details) {
            Product p = d.getProduct();
            int qty = d.getQty() != null ? d.getQty() : 1;
            totalItems += qty;
            double unitPrice = d.getUnitPrice() != null ? d.getUnitPrice() : 0.0;
            double lineTotal = Math.round(unitPrice * qty * 100.0) / 100.0;

            String pName = (p != null && p.getName() != null) ? p.getName() : "Jewellery Piece";
            String itemType = (p != null && p.getItemType() != null) ? p.getItemType() : "GOLD";
            String karat = (p != null && p.getMaterial() != null) ? p.getMaterial() : ("IMITATION".equalsIgnoreCase(itemType) ? "18K PVD" : "22K Gold");

            itemDTOs.add(lk.ijse.jewellery_management_system.dto.OrderItemResponseDTO.builder()
                    .productId(p != null ? p.getId() : null)
                    .productName(pName)
                    .itemType(itemType)
                    .karat(karat)
                    .qty(qty)
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .build());
        }

        String displayCustomerName = (o.getCustomerName() != null && !o.getCustomerName().isBlank())
                ? o.getCustomerName()
                : (c != null && c.getName() != null ? c.getName() : "Walk-in Boutique Client");

        String displayCustomerContact = (o.getCustomerContact() != null && !o.getCustomerContact().isBlank())
                ? o.getCustomerContact()
                : (c != null && c.getContact() != null ? c.getContact() : "");

        String effectiveStatus = o.getStatus() != null ? o.getStatus() : "APPROVED";
        String effectiveOrderType = o.getOrderType() != null ? o.getOrderType() : "POS";
        String effectiveOrderRef = (o.getOrderRef() != null && !o.getOrderRef().isBlank())
                ? o.getOrderRef()
                : "#AUR-" + (o.getOrderDate() != null ? o.getOrderDate().getYear() : 2026) + "-" + String.format("%04d", o.getId());

        return OrderResponseDTO.builder()
                .id(o.getId())
                .orderDate(o.getOrderDate())
                .totalAmount(o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                .discount(o.getDiscount() != null ? o.getDiscount() : 0.0)
                .customerId(c != null ? c.getId() : null)
                .customerName(displayCustomerName)
                .customerContact(displayCustomerContact)
                .orderType(effectiveOrderType)
                .orderRef(effectiveOrderRef)
                .deliveryAddress(o.getDeliveryAddress())
                .paymentMethod(o.getPaymentMethod())
                .status(effectiveStatus)
                .totalItems(totalItems)
                .items(itemDTOs)
                .build();
    }
}
