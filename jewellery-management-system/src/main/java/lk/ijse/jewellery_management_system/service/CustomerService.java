package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    void saveCustomer(CustomerDTO dto);

    List<CustomerDTO> getAllCustomers();

    Long getCustomerCount();

    void updateCustomer(Integer id, CustomerDTO dto);

    void deleteCustomer(Integer id);
}
