package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    String saveProduct(ProductDTO dto);

    String updateProduct(Integer id, ProductDTO dto);

    String deleteProduct(Integer id);

    List<ProductDTO> getAllProducts();

    ProductDTO getProductById(Integer id);
}