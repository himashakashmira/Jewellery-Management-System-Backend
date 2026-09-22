package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.ProductDTO;
import lk.ijse.jewellery_management_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final ProductService productService;

    @PostMapping("/save")
    public String save(@RequestBody ProductDTO dto) {
        return productService.saveProduct(dto);
    }

    @PutMapping("/update/{id}")
    public String update(@PathVariable Integer id, @RequestBody ProductDTO dto) {
        return productService.updateProduct(id, dto);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/all")
    public List<ProductDTO> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/sold")
    public List<ProductDTO> getSold() {
        return productService.getSoldProducts();
    }

    @PutMapping("/{id}/sell")
    public String markSold(@PathVariable Integer id) {
        return productService.markProductAsSold(id);
    }

    @GetMapping("/low-stock")
    public List<ProductDTO> getLowStock(@RequestParam(required = false, defaultValue = "3") Integer threshold) {
        return productService.getLowStockProducts(threshold);
    }

    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }
}