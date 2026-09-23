package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepairDTO {
    private Integer id;
    private String itemName;
    private String description;
    private String status; // Received, Melting, Crafting, Ready
    private LocalDate receivedDate; // Date handed over for repair
    private LocalDate returnDate;   // Target completion date
    private Double estimatedCost;
    private Integer customerId;     // link with customer
    private String customerName;    // Patron Name
    private String customerContact; // Mobile number (required)
    private String customerEmail;   // Email (optional)
    private String customerAddress; // Address (optional)
}