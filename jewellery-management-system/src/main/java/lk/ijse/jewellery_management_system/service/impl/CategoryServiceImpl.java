package lk.ijse.jewellery_management_system.service.impl;

import lk.ijse.jewellery_management_system.dto.CategoryDTO;
import lk.ijse.jewellery_management_system.entity.Category;
import lk.ijse.jewellery_management_system.repository.CategoryRepository;
import lk.ijse.jewellery_management_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public void saveCategory(CategoryDTO dto) {
        Category category = Category.builder().name(dto.getName()).build();
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategoryDTO(c.getId(), c.getName()))
                .toList();
    }
}