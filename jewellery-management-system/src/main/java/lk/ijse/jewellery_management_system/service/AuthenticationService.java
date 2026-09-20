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

    // Register Logic (Save to User table AND Customer table)
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();
        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.CUSTOMER) {
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
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .role(savedUser.getRole().name())
                .build();
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