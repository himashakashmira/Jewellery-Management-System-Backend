package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.Customer;
import lk.ijse.jewellery_management_system.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Integer> {
    SavingsAccount findByCustomer(Customer customer);
}
