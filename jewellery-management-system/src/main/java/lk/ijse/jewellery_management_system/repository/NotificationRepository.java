package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    // find notifications for a specific user
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);
}