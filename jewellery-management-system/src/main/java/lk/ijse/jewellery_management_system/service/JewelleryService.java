package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.CategoryDTO;
import lk.ijse.jewellery_management_system.dto.GoldRateDTO;
import lk.ijse.jewellery_management_system.dto.ProductDTO;

import java.util.List;

public interface JewelleryService {
    String saveProduct(ProductDTO productDTO);

    List<ProductDTO> getAllProducts();

    void saveCategory(CategoryDTO categoryDTO);

    List<CategoryDTO> getAllCategories();

    void updateGoldRate(GoldRateDTO goldRateDTO);

    Double calculateProductPrice(Integer productId);
}