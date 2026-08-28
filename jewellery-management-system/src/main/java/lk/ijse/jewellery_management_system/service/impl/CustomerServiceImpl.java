package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.CustomerDTO;
import lk.ijse.jewellery_management_system.entity.Customer;
import lk.ijse.jewellery_management_system.repository.CustomerRepository;
import lk.ijse.jewellery_management_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}