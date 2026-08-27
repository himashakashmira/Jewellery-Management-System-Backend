package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.CustomerDTO;
import lk.ijse.jewellery_management_system.service.JewelleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final JewelleryService jewelleryService;

    @PostMapping("/save")
    public String save(@RequestBody CustomerDTO dto) {
        jewelleryService.saveCustomer(dto);
        return "Customer saved successfully!";
    }

}