package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.OrderDTO;
import lk.ijse.jewellery_management_system.dto.OrderResponseDTO;
import lk.ijse.jewellery_management_system.dto.OrderStatsDTO;
import lk.ijse.jewellery_management_system.dto.ReportSummaryDTO;

import java.util.List;

public interface OrderService {
    String placeOrder(OrderDTO dto);

    OrderStatsDTO getOrderStats();

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO getOrderById(Integer id);

    ReportSummaryDTO getReportSummary();
}
