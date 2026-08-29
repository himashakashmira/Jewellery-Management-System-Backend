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
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final StockRepository stockRepository;
    private final GoldRateService goldRateService;

    @Override
    public String placeOrder(OrderDTO dto) {
        // 1. find customer
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow();

        // 2. create main order object
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .customer(customer)
                .discount(dto.getDiscount())
                .totalAmount(0.0) // set initial 0
                .build();

        Order savedOrder = orderRepository.save(order);
        Double finalTotal = 0.0;

        // 3. process each item in the order
        for (OrderDetailDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId()).orElseThrow();

            // get current price using our logic service
            Double price = goldRateService.calculateProductPrice(product.getId());

            // save order details
            OrderDetail detail = OrderDetail.builder()
                    .order(savedOrder)
                    .product(product)
                    .qty(itemDto.getQty())
                    .unitPrice(price)
                    .build();
            orderDetailRepository.save(detail);

            // update inventory stock
            Stock stock = stockRepository.findByProduct(product);
            stock.setQuantity(stock.getQuantity() - itemDto.getQty());
            stockRepository.save(stock);

            finalTotal += (price * itemDto.getQty());
        }

        // 4. update final order amount after discount
        savedOrder.setTotalAmount(finalTotal - dto.getDiscount());
        orderRepository.save(savedOrder);

        // 5. add loyalty points (logic: 1 point for every 1000 Rs)
        int points = (int) (finalTotal / 1000);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
        customerRepository.save(customer);

        return "Order Placed Successfully. ID: " + savedOrder.getId();
    }
}
