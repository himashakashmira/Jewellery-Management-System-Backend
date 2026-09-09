package lk.ijse.jewellery_management_system.config;

import lk.ijse.jewellery_management_system.entity.Category;
import lk.ijse.jewellery_management_system.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // if categories are empty, add default values
        if (categoryRepository.count() == 0) {
            categoryRepository.save(Category.builder().name("Rings").build());
            categoryRepository.save(Category.builder().name("Necklace").build());
            categoryRepository.save(Category.builder().name("Bangles").build());
            categoryRepository.save(Category.builder().name("Pendants").build());
            categoryRepository.save(Category.builder().name("Earrings").build());

            System.out.println("Default Categories Added Successfully!");
        }
    }
}