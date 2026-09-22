package lk.ijse.jewellery_management_system.repository;


import lk.ijse.jewellery_management_system.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerId(Integer customerId);
}
