package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.SavingsDepositDTO;

public interface SavingsService {
    String depositMoney(SavingsDepositDTO dto);

    Double getCustomerGoldBalance(Integer customerId);
}