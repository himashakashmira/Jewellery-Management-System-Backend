package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.config.JwtService;
import lk.ijse.jewellery_management_system.dto.AuthRequestDTO;
import lk.ijse.jewellery_management_system.dto.AuthResponseDTO;
import lk.ijse.jewellery_management_system.dto.RegisterRequestDTO;
import lk.ijse.jewellery_management_system.entity.Customer;
import lk.ijse.jewellery_management_system.entity.User;
import lk.ijse.jewellery_management_system.enumeration.Role;
import lk.ijse.jewellery_management_system.repository.CustomerRepository;
import lk.ijse.jewellery_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final NotificationService notificationService;

    // Register Logic (Save to User table AND Customer table)
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf("ROLE_" + request.getRole()))
                .build();
        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.ROLE_CUSTOMER) {
            Customer customer = Customer.builder()
                    .name(request.getFullName())
                    .email(request.getUsername())
                    .contact(request.getContact())
                    .loyaltyPoints(0)
                    .user(savedUser)
                    .build();
            customerRepository.save(customer);
        }

        var jwtToken = jwtService.generateToken(savedUser);

        // Automatically dispatch welcome email to the newly registered member
        sendWelcomeEmail(savedUser, request.getFullName(), request.getContact());

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .role(savedUser.getRole().name())
                .build();
    }

    private void sendWelcomeEmail(User user, String fullName, String contact) {
        String email = user.getUsername();
        if (email == null || email.isBlank()) {
            return;
        }

        String displayName = (fullName != null && !fullName.isBlank()) ? fullName : "Aurum Member";
        String roleTitle = user.getRole() == Role.ROLE_CUSTOMER ? "Aurum Heritage VIP Member" : "Aurum Atelier Team Member";

        String subject = "AURUM JEWELS | Welcome to Aurum - Member Registered Successfully";
        String body = "Dear " + displayName + ",\n\n"
                + "Welcome to AURUM JEWELS Atelier. Your Aurum membership account has been registered successfully!\n\n"
                + "Exclusive Membership Details:\n"
                + "• Member Name: " + displayName + "\n"
                + "• Registered Email: " + email + "\n"
                + "• Contact: " + (contact != null && !contact.isBlank() ? contact : "Provided upon visit") + "\n"
                + "• Privilege Tier: " + roleTitle + "\n\n"
                + "You can now log in to AURUM Atelier to explore our fine high-jewellery collections, track bespoke custom orders and restorations in real time, and access our private vault services.\n\n"
                + "If you did not initiate this registration, please contact our concierge immediately.\n\n"
                + "Warmest regards,\n"
                + "AURUM JEWELS Concierge & Membership Services\n"
                + "48 Galle Face Court, Colombo 03 | +94 11 234 5678";

        notificationService.notifyCustomer(user.getId(), email, subject, body);
    }

    // Authenticate/Login Logic
    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .role(user.getRole().name())
                .build();
    }
}