package lk.ijse.jewellery_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.jewellery_management_system.dto.SavingsDepositDTO;
import lk.ijse.jewellery_management_system.entity.Customer;
import lk.ijse.jewellery_management_system.entity.GoldRate;
import lk.ijse.jewellery_management_system.entity.SavingsAccount;
import lk.ijse.jewellery_management_system.entity.SavingsTransaction;
import lk.ijse.jewellery_management_system.repository.CustomerRepository;
import lk.ijse.jewellery_management_system.repository.GoldRateRepository;
import lk.ijse.jewellery_management_system.repository.SavingsAccountRepository;
import lk.ijse.jewellery_management_system.repository.SavingsTransactionRepository;
import lk.ijse.jewellery_management_system.service.SavingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SavingsServiceImpl implements SavingsService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsTransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final GoldRateRepository goldRateRepository;

    @Override
    public String depositMoney(SavingsDepositDTO dto) {
        // 1. find customer
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow();

        // 2. find latest gold rate to convert money to weight
        GoldRate currentRate = goldRateRepository.findAll().stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .findFirst().orElseThrow(() -> new RuntimeException("Update gold rate first"));

        // 3. Logic: convert Cash to Gold Weight (grams)
        // weight = money / today's rate
        Double gainedWeight = dto.getAmount() / currentRate.getRate22K();

        // 4. update or create savings account
        SavingsAccount account = savingsAccountRepository.findByCustomer(customer);
        if (account == null) {
            account = SavingsAccount.builder()
                    .customer(customer)
                    .totalGoldBalanceGrams(0.0)
                    .totalCashPaid(0.0)
                    .startDate(LocalDate.now())
                    .build();
        }

        account.setTotalGoldBalanceGrams(account.getTotalGoldBalanceGrams() + gainedWeight);
        account.setTotalCashPaid(account.getTotalCashPaid() + dto.getAmount());
        savingsAccountRepository.save(account);

        // 5. save transaction record
        SavingsTransaction tx = SavingsTransaction.builder()
                .savingsAccount(account)
                .paidAmount(dto.getAmount())
                .goldWeightGained(gainedWeight)
                .goldRateAtTime(currentRate.getRate22K())
                .transactionDate(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);

        return "Successfully deposited. Gained Gold: " + String.format("%.3f", gainedWeight) + " g";
    }

    @Override
    public Double getCustomerGoldBalance(Integer customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        SavingsAccount account = savingsAccountRepository.findByCustomer(customer);
        return (account != null) ? account.getTotalGoldBalanceGrams() : 0.0;
    }
}