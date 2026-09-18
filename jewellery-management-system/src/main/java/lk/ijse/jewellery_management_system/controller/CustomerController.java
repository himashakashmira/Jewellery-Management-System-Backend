package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.CustomerDTO;
import lk.ijse.jewellery_management_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/save")
    public String save(@RequestBody CustomerDTO dto) {
        customerService.saveCustomer(dto);
        return "Customer saved successfully!";
    }

    @GetMapping("/all")
    public List<CustomerDTO> getAll() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/count")
    public Long getCount() {
        return customerService.getCustomerCount();
    }

    // update an existing customer
    @PutMapping("/update/{id}")
    public String update(@PathVariable Integer id, @RequestBody CustomerDTO dto) {
        customerService.updateCustomer(id, dto);
        return "Customer updated successfully!";
    }

    // remove a customer
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return "Customer deleted successfully!";
    }
}