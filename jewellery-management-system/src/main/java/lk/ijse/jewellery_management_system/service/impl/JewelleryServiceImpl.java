package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.CategoryDTO;
import lk.ijse.jewellery_management_system.dto.ProductDTO;
import lk.ijse.jewellery_management_system.entity.Category;
import lk.ijse.jewellery_management_system.entity.Product;
import lk.ijse.jewellery_management_system.repository.ProductRepository;
import lk.ijse.jewellery_management_system.repository.CategoryRepository;
import lk.ijse.jewellery_management_system.service.JewelleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JewelleryServiceImpl implements JewelleryService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public String saveProduct(ProductDTO dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .weight(dto.getWeight())
                .wastage(dto.getWastage())
                .labourCost(dto.getLabourCost())
                .category(categoryRepository.findById(dto.getCategoryId()).orElse(null))
                .build();

        productRepository.save(product);
        return "Product Saved Successfully!";
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> allProducts = productRepository.findAll();
        List<ProductDTO> dtoList = new ArrayList<>();

        for (Product p : allProducts) {
            dtoList.add(new ProductDTO(
                    p.getId(), p.getName(), p.getWeight(),
                    p.getWastage(), p.getLabourCost(),
                    p.getCategory().getId()
            ));
        }
        return dtoList;
    }

    @Override
    public void saveCategory(CategoryDTO dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .build();
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryDTO(c.getId(), c.getName()))
                .toList();
    }
}