package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.GoldRateDTO;
import lk.ijse.jewellery_management_system.entity.GoldRate;
import lk.ijse.jewellery_management_system.entity.Product;
import lk.ijse.jewellery_management_system.repository.GoldRateRepository;
import lk.ijse.jewellery_management_system.repository.ProductRepository;
import lk.ijse.jewellery_management_system.service.GoldRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoldRateServiceImpl implements GoldRateService {
    private final GoldRateRepository goldRateRepository;
    private final ProductRepository productRepository;

    @Override
    public void updateGoldRate(GoldRateDTO dto) {
        GoldRate rate = GoldRate.builder()
                .rate22K(dto.getRate22K())
                .rate24K(dto.getRate24K())
                .updatedAt(LocalDateTime.now())
                .build();
        goldRateRepository.save(rate);
    }

    @Override
    public Double calculateProductPrice(Integer productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        GoldRate currentRate = goldRateRepository.findAll().stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .findFirst().orElseThrow();

        // business logic for price calculation
        Double goldValue = product.getWeight() * currentRate.getRate22K();
        Double wastageValue = goldValue * (product.getWastage() / 100);
        return goldValue + wastageValue + product.getLabourCost();
    }
}