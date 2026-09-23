package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.CustomerDTO;
import lk.ijse.jewellery_management_system.entity.Customer;
import lk.ijse.jewellery_management_system.repository.CustomerRepository;
import lk.ijse.jewellery_management_system.service.CustomerService;
import lk.ijse.jewellery_management_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;

    @Override
    public void saveCustomer(CustomerDTO dto) {
        Customer customer = Customer.builder()
                .name(dto.getName())
                .contact(dto.getContact())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .loyaltyPoints(0)
                .build();
        Customer saved = customerRepository.save(customer);

        // Send welcome email if customer email is provided
        if (saved.getEmail() != null && !saved.getEmail().isBlank()) {
            String patronName = (saved.getName() != null && !saved.getName().isBlank()) ? saved.getName() : "Valued Patron";
            String subject = "AURUM JEWELS | Welcome to Aurum - Member Registered Successfully";
            String body = "Dear " + patronName + ",\n\n"
                    + "Welcome to AURUM JEWELS Atelier. Your Aurum VIP Patron membership has been registered successfully!\n\n"
                    + "Patron Registry Summary:\n"
                    + "• Patron Name: " + patronName + "\n"
                    + "• Registered Email: " + saved.getEmail() + "\n"
                    + "• Contact Number: " + (saved.getContact() != null ? saved.getContact() : "—") + "\n"
                    + "• Atelier Privileges: Complimentary cleaning & inspection, bespoke high-jewellery commissions, and priority bench access.\n\n"
                    + "We look forward to welcoming you at our Flagship Boutique.\n\n"
                    + "Warm regards,\n"
                    + "AURUM JEWELS Atelier\n"
                    + "48 Galle Face Court, Colombo 03 | +94 11 234 5678";

            notificationService.sendEmail(saved.getEmail().trim(), subject, body);
        }
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
