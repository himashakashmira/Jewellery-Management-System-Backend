package lk.ijse.jewellery_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.jewellery_management_system.dto.OrderDTO;
import lk.ijse.jewellery_management_system.dto.OrderDetailDTO;
import lk.ijse.jewellery_management_system.entity.*;
import lk.ijse.jewellery_management_system.repository.*;
import lk.ijse.jewellery_management_system.service.GoldRateService;
import lk.ijse.jewellery_management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        // Get current customer
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));

        // Create the main Order
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .customer(customer)
                .discount(dto.getDiscount())
                .totalAmount(0.0) // initial 0, we calculate later
                .build();

        Order savedOrder = orderRepository.save(order);
        Double finalBillAmount = 0.0;

        // Process each item in the cart
        for (OrderDetailDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).orElseThrow();

            // Get price from our GoldRate calculation service
            Double currentPrice = goldRateService.calculateProductPrice(product.getId());

            // Save Order Details
            OrderDetail detail = OrderDetail.builder()
                    .order(savedOrder)
                    .product(product)
                    .qty(itemDto.getQty())
                    .unitPrice(currentPrice)
                    .build();
            orderDetailRepository.save(detail);

            // Update stock (Reduce Qty)
            Stock stock = stockRepository.findByProduct(product);
            if(stock != null) {
                stock.setQuantity(stock.getQuantity() - itemDto.getQty());
                stockRepository.save(stock);
            }

            finalBillAmount += (currentPrice * itemDto.getQty());
        }

        // Update the final amount in the main order
        savedOrder.setTotalAmount(finalBillAmount - dto.getDiscount());
        orderRepository.save(savedOrder);

        // Add loyalty points
        int points = (int) (finalBillAmount / 1000);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
        customerRepository.save(customer);

        return "Order " + savedOrder.getId() + " Placed Successfully!";
    }
}
