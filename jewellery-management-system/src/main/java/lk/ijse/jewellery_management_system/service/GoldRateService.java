package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.GoldRateDTO;

public interface GoldRateService {
    void updateGoldRate(GoldRateDTO dto);

    Double calculateProductPrice(Integer productId);
}