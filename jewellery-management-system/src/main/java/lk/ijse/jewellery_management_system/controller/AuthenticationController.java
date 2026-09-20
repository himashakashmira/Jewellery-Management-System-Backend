package lk.ijse.jewellery_management_system.controller;

import lk.ijse.jewellery_management_system.dto.AuthRequestDTO;
import lk.ijse.jewellery_management_system.dto.AuthResponseDTO;
import lk.ijse.jewellery_management_system.dto.RegisterRequestDTO;
import lk.ijse.jewellery_management_system.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> authenticate(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
