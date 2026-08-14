package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
