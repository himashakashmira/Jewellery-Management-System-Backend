package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByContact(String contact);
    Optional<Customer> findByEmail(String email);
}