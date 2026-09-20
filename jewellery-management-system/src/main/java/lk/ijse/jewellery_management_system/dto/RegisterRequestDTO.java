package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDTO {
    private String fullName;
    private String username; // email
    private String password;
    private String contact;
    private String role; // CUSTOMER or STAFF
}