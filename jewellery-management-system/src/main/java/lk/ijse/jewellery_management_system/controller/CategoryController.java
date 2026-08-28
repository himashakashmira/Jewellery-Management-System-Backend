package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.CategoryDTO;
import lk.ijse.jewellery_management_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public String saveCategory(@RequestBody CategoryDTO categoryDTO) {
        categoryService.saveCategory(categoryDTO);
        return "Category Saved!";
    }

    @GetMapping
    public List<CategoryDTO> getAll() {
        return categoryService.getAllCategories();
    }
}