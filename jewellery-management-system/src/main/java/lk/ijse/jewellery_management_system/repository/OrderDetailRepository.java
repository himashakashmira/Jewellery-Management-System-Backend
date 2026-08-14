package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
}
