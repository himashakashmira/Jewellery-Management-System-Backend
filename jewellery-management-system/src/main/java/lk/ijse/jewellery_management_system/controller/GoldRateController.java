package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.GoldRateDTO;
import lk.ijse.jewellery_management_system.service.GoldRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gold-rates")
@RequiredArgsConstructor
public class GoldRateController {

    private final GoldRateService goldRateService;

    @PostMapping("/update")
    public String update(@RequestBody GoldRateDTO dto) {
        goldRateService.updateGoldRate(dto);
        return "Today's Gold Rate Updated!";
    }

    @GetMapping("/price/{productId}")
    public Double getPrice(@PathVariable Integer productId) {
        return goldRateService.calculateProductPrice(productId);
    }
}