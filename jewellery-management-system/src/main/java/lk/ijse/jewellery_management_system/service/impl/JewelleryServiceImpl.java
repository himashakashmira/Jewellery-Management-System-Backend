package lk.ijse.jewellery_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.jewellery_management_system.dto.*;
import lk.ijse.jewellery_management_system.entity.*;
import lk.ijse.jewellery_management_system.repository.*;
import lk.ijse.jewellery_management_system.service.JewelleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JewelleryServiceImpl implements JewelleryService {

    private final ProductRepository productRepository;
    private final GoldRateRepository goldRateRepository;
    private final CategoryRepository categoryRepository;
    private final RepairRepository repairRepository;
    private final CustomerRepository customerRepository;


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
                .orElseThrow(() -> new RuntimeException("Product not found"));

        GoldRate currentRate = goldRateRepository.findAll().stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Please update gold rate first!"));

        // calculate price - Core Logic
        Double goldValue = product.getWeight() * currentRate.getRate22K();
        Double wastageValue = goldValue * (product.getWastage() / 100);

        return goldValue + wastageValue + product.getLabourCost();
    }

    @Override
    @Transactional // use this for update operations
    public void registerRepair(RepairDTO dto) {
        // find customer first
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // create repair entity
        Repair repair = Repair.builder()
                .itemName(dto.getItemName())
                .description(dto.getDescription())
                .status("Received") // default status
                .receivedDate(LocalDate.now())
                .estimatedCost(dto.getEstimatedCost())
                .customer(customer)
                .build();

        repairRepository.save(repair);
    }

    @Override
    @Transactional
    public void updateRepairStatus(Integer repairId, String newStatus) {
        // get repair from database
        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new RuntimeException("Repair job not found"));

        // update the status string
        repair.setStatus(newStatus);

        // logic: if status is 'Ready', we can add more logic later like sending SMS
        if (newStatus.equalsIgnoreCase("Ready")) {
            System.out.println("Repair is finished for: " + repair.getItemName());
        }

        repairRepository.save(repair);
    }

    // get all repair records from database
    @Override
    public List<RepairDTO> getAllRepairs() {
        List<Repair> all = repairRepository.findAll();
        List<RepairDTO> dtoList = new ArrayList<>();

        // convert entity list to dto list
        for (Repair r : all) {
            dtoList.add(new RepairDTO(
                    r.getId(),
                    r.getItemName(),
                    r.getDescription(),
                    r.getStatus(),
                    r.getReceivedDate(),
                    r.getEstimatedCost(),
                    r.getCustomer().getId()
            ));
        }
        return dtoList;
    }

    // save customer details to database
    @Override
    public void saveCustomer(CustomerDTO dto) {
        Customer customer = Customer.builder()
                .name(dto.getName())
                .contact(dto.getContact())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .loyaltyPoints(0) // initial points 0
                .build();
        customerRepository.save(customer);
    }
}