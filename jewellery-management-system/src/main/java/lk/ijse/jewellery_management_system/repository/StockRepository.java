package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Integer> {
}
