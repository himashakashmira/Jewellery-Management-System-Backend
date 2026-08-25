package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.GoldRateDTO;
import lk.ijse.jewellery_management_system.service.JewelleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gold-rates")
@RequiredArgsConstructor
public class GoldRateController {

    private final JewelleryService jewelleryService;

    @PostMapping("/update")
    public String update(@RequestBody GoldRateDTO dto) {
        jewelleryService.updateGoldRate(dto);
        return "Today's Gold Rate Updated!";
    }

    @GetMapping("/price/{productId}")
    public Double getPrice(@PathVariable Integer productId) {
        return jewelleryService.calculateProductPrice(productId);
    }
}