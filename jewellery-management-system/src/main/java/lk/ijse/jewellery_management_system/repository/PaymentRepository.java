package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
