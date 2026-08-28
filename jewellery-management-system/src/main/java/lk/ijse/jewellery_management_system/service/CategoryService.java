package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    void saveCategory(CategoryDTO dto);

    List<CategoryDTO> getAllCategories();
}