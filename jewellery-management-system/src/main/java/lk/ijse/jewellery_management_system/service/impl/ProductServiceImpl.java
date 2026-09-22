package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.ProductDTO;
import lk.ijse.jewellery_management_system.entity.Product;
import lk.ijse.jewellery_management_system.entity.Stock;
import lk.ijse.jewellery_management_system.repository.CategoryRepository;
import lk.ijse.jewellery_management_system.repository.ProductRepository;
import lk.ijse.jewellery_management_system.repository.StockRepository;
import lk.ijse.jewellery_management_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;

    // save new jewellery item to database and initialize stock
    @Override
    public String saveProduct(ProductDTO dto) {
        String initialStatus = (dto.getStatus() != null && !dto.getStatus().isBlank())
                ? dto.getStatus() : "AVAILABLE";

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
                .status(initialStatus)
                .build();

        Product savedProduct = productRepository.save(product);

        // Record stock in inventory database
        int stockQty = (dto.getStock() != null && dto.getStock() >= 0) ? dto.getStock() : 10;
        Stock stock = Stock.builder()
                .product(savedProduct)
                .quantity(stockQty)
                .lastUpdated(LocalDateTime.now())
                .build();
        stockRepository.save(stock);

        return "Item saved successfully";
    }

    // update existing item details and stock
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
        if (dto.getStatus() != null) product.setStatus(dto.getStatus());

        // also update the category if a new categoryId was provided
        if (dto.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(dto.getCategoryId()).orElse(null));
        }

        productRepository.save(product);

        // update stock quantity if provided
        if (dto.getStock() != null) {
            Stock stock = stockRepository.findByProduct(product);
            if (stock == null) {
                stock = Stock.builder()
                        .product(product)
                        .quantity(dto.getStock())
                        .lastUpdated(LocalDateTime.now())
                        .build();
            } else {
                stock.setQuantity(dto.getStock());
                stock.setLastUpdated(LocalDateTime.now());
            }
            stockRepository.save(stock);
        }

        return "Item updated successfully";
    }

    // remove item and its stock record from inventory
    @Override
    public String deleteProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            Stock stock = stockRepository.findByProduct(product);
            if (stock != null) {
                stockRepository.delete(stock);
            }
            productRepository.delete(product);
        }
        return "Item deleted";
    }

    // get all active stock items as DTO list (sold gold pieces are excluded from active inventory UI)
    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .filter(p -> !"SOLD".equalsIgnoreCase(p.getStatus()))
                .map(p -> {
                    Stock stock = stockRepository.findByProduct(p);
                    Integer stockQty = (stock != null) ? stock.getQuantity() : 1;
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
                            p.getMaterial(),
                            stockQty,
                            p.getStatus() != null ? p.getStatus() : "AVAILABLE");
                })
                .collect(Collectors.toList());
    }

    // find one item by its ID with stock count
    @Override
    public ProductDTO getProductById(Integer id) {
        Product p = productRepository.findById(id).orElseThrow();
        Stock stock = stockRepository.findByProduct(p);
        Integer stockQty = (stock != null) ? stock.getQuantity() : 1;
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
                p.getMaterial(),
                stockQty,
                p.getStatus() != null ? p.getStatus() : "AVAILABLE");
    }

    @Override
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        int maxLimit = (threshold != null && threshold >= 0) ? threshold : 3;
        return getAllProducts().stream()
                .filter(p -> p.getStock() != null && p.getStock() <= maxLimit)
                .collect(Collectors.toList());
    }

    // Get all sold products (for official ledger and sales reports)
    @Override
    public List<ProductDTO> getSoldProducts() {
        return productRepository.findAll().stream()
                .filter(p -> "SOLD".equalsIgnoreCase(p.getStatus()))
                .map(p -> {
                    Stock stock = stockRepository.findByProduct(p);
                    Integer stockQty = (stock != null) ? stock.getQuantity() : 0;
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
                            p.getMaterial(),
                            stockQty,
                            "SOLD");
                })
                .collect(Collectors.toList());
    }

    // Mark a gold or bespoke piece as SOLD in database
    @Override
    public String markProductAsSold(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        product.setStatus("SOLD");
        productRepository.save(product);

        Stock stock = stockRepository.findByProduct(product);
        if (stock != null) {
            stock.setQuantity(0);
            stock.setLastUpdated(LocalDateTime.now());
            stockRepository.save(stock);
        }

        return "Product " + id + " (" + product.getName() + ") marked as SOLD in database.";
    }
}