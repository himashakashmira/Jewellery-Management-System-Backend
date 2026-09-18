package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.CustomerDTO;
import lk.ijse.jewellery_management_system.entity.Customer;
import lk.ijse.jewellery_management_system.repository.CustomerRepository;
import lk.ijse.jewellery_management_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public void saveCustomer(CustomerDTO dto) {
        Customer customer = Customer.builder()
                .name(dto.getName())
                .contact(dto.getContact())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .loyaltyPoints(0)
                .build();
        customerRepository.save(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(c -> new CustomerDTO(c.getId(), c.getName(), c.getContact(), c.getEmail(), c.getAddress()))
                .toList();
    }

    @Override
    public Long getCustomerCount() {
        return customerRepository.count();
    }

    // update existing customer details
    @Override
    public void updateCustomer(Integer id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setName(dto.getName());
        customer.setContact(dto.getContact());
        customer.setEmail(dto.getEmail());
        customer.setAddress(dto.getAddress());
        customerRepository.save(customer);
    }

    // remove a customer record
    @Override
    public void deleteCustomer(Integer id) {
        customerRepository.deleteById(id);
    }
}
