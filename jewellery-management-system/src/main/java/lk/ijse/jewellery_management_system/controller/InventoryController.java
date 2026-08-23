package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.ProductDTO;
import lk.ijse.jewellery_management_system.service.JewelleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final JewelleryService jewelleryService;

    @PostMapping
    public String saveProduct(@RequestBody ProductDTO productDTO) {
        return jewelleryService.saveProduct(productDTO);
    }

    @GetMapping
    public List<ProductDTO> getAll() {
        return jewelleryService.getAllProducts();
    }
}