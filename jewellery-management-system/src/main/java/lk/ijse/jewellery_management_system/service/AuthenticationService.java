package lk.ijse.jewellery_management_system.service;

import lk.ijse.jewellery_management_system.config.JwtService;
import lk.ijse.jewellery_management_system.dto.AuthRequestDTO;
import lk.ijse.jewellery_management_system.dto.AuthResponseDTO;
import lk.ijse.jewellery_management_system.entity.User;
import lk.ijse.jewellery_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // save new user/admin with encrypted password
    public AuthResponseDTO register(User request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // password encryption
                .role(request.getRole())
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }

    // authenticate existing user and provide token
    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }
}