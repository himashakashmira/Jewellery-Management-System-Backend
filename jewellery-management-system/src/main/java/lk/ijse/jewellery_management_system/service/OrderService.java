package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.OrderDTO;
import lk.ijse.jewellery_management_system.dto.OrderStatsDTO;

public interface OrderService {
    String placeOrder(OrderDTO dto);

    OrderStatsDTO getOrderStats();
}
