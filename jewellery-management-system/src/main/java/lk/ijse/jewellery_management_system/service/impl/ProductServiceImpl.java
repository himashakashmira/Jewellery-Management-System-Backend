package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.ProductDTO;
import lk.ijse.jewellery_management_system.entity.Product;
import lk.ijse.jewellery_management_system.repository.CategoryRepository;
import lk.ijse.jewellery_management_system.repository.ProductRepository;
import lk.ijse.jewellery_management_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // save new jewellery item to database
    @Override
    public String saveProduct(ProductDTO dto) {
        Product product = Product.builder()
                .name(dto.getName())
                .weight(dto.getWeight())
                .wastage(dto.getWastage())
                .labourCost(dto.getLabourCost())
                .itemType(dto.getItemType() != null ? dto.getItemType() : "GOLD")
                .image(dto.getImage())
                .price(dto.getPrice())
                .material(dto.getMaterial())
                .category(dto.getCategoryId() != null ? categoryRepository.findById(dto.getCategoryId()).orElse(null) : null)
                .build();

        productRepository.save(product);
        return "Item saved successfully";
    }

    // update existing item details
    @Override
    public String updateProduct(Integer id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(dto.getName());
        product.setWeight(dto.getWeight());
        product.setWastage(dto.getWastage());
        product.setLabourCost(dto.getLabourCost());
        if (dto.getItemType() != null) product.setItemType(dto.getItemType());
        if (dto.getImage() != null) product.setImage(dto.getImage());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getMaterial() != null) product.setMaterial(dto.getMaterial());

        // also update the category if a new categoryId was provided
        if (dto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(dto.getCategoryId()).orElse(null));
        }

        productRepository.save(product);
        return "Item updated successfully";
    }

    // remove item from inventory
    @Override
    public String deleteProduct(Integer id) {
        productRepository.deleteById(id);
        return "Item deleted";
    }

    // get all stock items as DTO list
    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductDTO(
                        p.getId(),
                        p.getName(),
                        p.getWeight(),
                        p.getWastage(),
                        p.getLabourCost(),
                        p.getCategory() != null ? p.getCategory().getId() : null,
                        p.getItemType() != null ? p.getItemType() : "GOLD",
                        p.getImage(),
                        p.getPrice(),
                        p.getMaterial()))
                .collect(Collectors.toList());
    }

    // find one item by its ID
    @Override
    public ProductDTO getProductById(Integer id) {
        Product p = productRepository.findById(id).orElseThrow();
        return new ProductDTO(
                p.getId(),
                p.getName(),
                p.getWeight(),
                p.getWastage(),
                p.getLabourCost(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getItemType() != null ? p.getItemType() : "GOLD",
                p.getImage(),
                p.getPrice(),
                p.getMaterial());
    }
}