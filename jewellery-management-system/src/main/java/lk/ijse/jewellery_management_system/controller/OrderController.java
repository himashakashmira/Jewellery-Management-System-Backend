package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.OrderDTO;
import lk.ijse.jewellery_management_system.dto.OrderResponseDTO;
import lk.ijse.jewellery_management_system.dto.OrderStatsDTO;
import lk.ijse.jewellery_management_system.dto.ReportSummaryDTO;
import lk.ijse.jewellery_management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public String placeOrder(@RequestBody OrderDTO dto) {
        return orderService.placeOrder(dto);
    }

    @GetMapping("/stats")
    public OrderStatsDTO getStats() {
        return orderService.getOrderStats();
    }

    @GetMapping("/all")
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Integer id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/report")
    public ReportSummaryDTO getReportSummary() {
        return orderService.getReportSummary();
    }

    @PutMapping("/{id}/approve")
    public String approveOrder(@PathVariable Integer id) {
        return orderService.approveOrder(id);
    }

    @GetMapping("/imitation")
    public List<OrderResponseDTO> getImitationOrders() {
        return orderService.getImitationOrders();
    }
}

