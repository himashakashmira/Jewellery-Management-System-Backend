package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.OrderDTO;

public interface OrderService {
    String placeOrder(OrderDTO dto);
}
