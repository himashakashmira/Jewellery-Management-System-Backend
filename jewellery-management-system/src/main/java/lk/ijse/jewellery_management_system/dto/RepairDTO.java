package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairDTO {
    private Integer id;
    private String itemName;
    private String description;
    private String status; // Received, Melting, Crafting, Ready
    private LocalDate receivedDate;
    private Double estimatedCost;
    private Integer customerId; // link with customer
}