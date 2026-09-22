package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderId(Integer orderId);
    List<OrderDetail> findByProductId(Integer productId);
}
