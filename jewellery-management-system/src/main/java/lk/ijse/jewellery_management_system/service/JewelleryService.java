package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.*;

import java.util.List;

public interface JewelleryService {
    String saveProduct(ProductDTO productDTO);

    List<ProductDTO> getAllProducts();

    void saveCategory(CategoryDTO categoryDTO);

    List<CategoryDTO> getAllCategories();

    void updateGoldRate(GoldRateDTO goldRateDTO);

    Double calculateProductPrice(Integer productId);

    void registerRepair(RepairDTO repairDTO);

    void updateRepairStatus(Integer repairId, String newStatus);

    List<RepairDTO> getAllRepairs();

    void saveCustomer(CustomerDTO customerDTO);
}