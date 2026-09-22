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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // For imitation items or items with fixed price, return the fixed price
        if ("IMITATION".equalsIgnoreCase(product.getItemType()) || (product.getPrice() != null && product.getPrice() > 0 && (product.getWeight() == null || product.getWeight() == 0))) {
            return product.getPrice() != null ? product.getPrice() : 0.0;
        }

        GoldRate currentRate = goldRateRepository.findAll().stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .findFirst()
                .orElse(null);

        double rate22k = (currentRate != null && currentRate.getRate22K() != null) ? currentRate.getRate22K() : 43375.0;
        double weight = product.getWeight() != null ? product.getWeight() : 0.0;
        double wastage = product.getWastage() != null ? product.getWastage() : 0.0;
        double labour = product.getLabourCost() != null ? product.getLabourCost() : 0.0;

        // business logic for price calculation: goldValue + wastageValue + labourCost
        Double goldValue = weight * rate22k;
        Double wastageValue = goldValue * (wastage / 100.0);
        return Math.round((goldValue + wastageValue + labour) * 100.0) / 100.0;
    }

    @Override
    public GoldRateDTO getLatestRate() {
        GoldRate latestRate = goldRateRepository.findAll().stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .findFirst()
                .orElse(null);

        if (latestRate == null) {
            // Return standard baseline rate if not yet set in database
            return new GoldRateDTO(43375.0, 47125.0, LocalDateTime.now());
        }

        return new GoldRateDTO(latestRate.getRate22K(), latestRate.getRate24K(), latestRate.getUpdatedAt());
    }
}