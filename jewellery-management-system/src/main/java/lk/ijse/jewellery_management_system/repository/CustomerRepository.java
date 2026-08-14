package lk.ijse.jewellery_management_system.repository;

import lk.ijse.jewellery_management_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // පස්සේ කාලෙක Phone number එකෙන් customer ව හොයන්න ඕන වුණොත් මෙතනට methods දාන්න පුළුවන්
}