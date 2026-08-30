package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.SavingsDepositDTO;
import lk.ijse.jewellery_management_system.service.SavingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/savings")
@RequiredArgsConstructor
public class SavingsController {

    private final SavingsService savingsService;

    @PostMapping("/deposit")
    public String deposit(@RequestBody SavingsDepositDTO dto) {
        return savingsService.depositMoney(dto);
    }

    @GetMapping("/balance/{customerId}")
    public Double getBalance(@PathVariable Integer customerId) {
        return savingsService.getCustomerGoldBalance(customerId);
    }
}