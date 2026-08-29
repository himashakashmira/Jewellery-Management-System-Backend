package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.OrderDTO;
import lk.ijse.jewellery_management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public String placeOrder(@RequestBody OrderDTO dto) {
        return orderService.placeOrder(dto);
    }
}
