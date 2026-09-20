package lk.ijse.jewellery_management_system.config;

import lk.ijse.jewellery_management_system.entity.Category;
import lk.ijse.jewellery_management_system.entity.User;
import lk.ijse.jewellery_management_system.enumeration.Role;
import lk.ijse.jewellery_management_system.repository.CategoryRepository;
import lk.ijse.jewellery_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Initializing Default Categories
        if (categoryRepository.count() == 0) {
            categoryRepository.save(Category.builder().name("Rings").build());
            categoryRepository.save(Category.builder().name("Necklace").build());
            categoryRepository.save(Category.builder().name("Bangles").build());
            categoryRepository.save(Category.builder().name("Pendants").build());
            categoryRepository.save(Category.builder().name("Earrings").build());

            System.out.println("Default Categories Added Successfully!");
        }

        // Initializing Default System Users (Admin and Staff)
        if (userRepository.count() == 0) {
            // create master admin account
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123")) // hashed password
                    .role(Role.ROLE_ADMIN)
                    .build());

            // create master staff account for daily operations
            userRepository.save(User.builder()
                    .username("staff")
                    .password(passwordEncoder.encode("staff123")) // hashed password
                    .role(Role.ROLE_STAFF)
                    .build());

            System.out.println("✅ Default accounts created: admin/admin123 and staff/staff123");
        }
    }
}