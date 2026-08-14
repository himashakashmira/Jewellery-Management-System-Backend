package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
}
