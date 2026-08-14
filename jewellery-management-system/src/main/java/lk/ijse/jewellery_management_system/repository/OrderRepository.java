package lk.ijse.jewellery_management_system.repository;


import lk.ijse.jewellery_management_system.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
