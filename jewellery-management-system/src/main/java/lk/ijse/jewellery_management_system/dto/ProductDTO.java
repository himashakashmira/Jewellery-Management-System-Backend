package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Integer id;
    private String name;
    private Double weight;
    private Double wastage;
    private Double labourCost;
    private Integer categoryId;

    private String itemType;   // "GOLD" or "IMITATION"
    private String image;      // Image URL or base64 data
    private Double price;      // Retail fixed price for store items
    private String material;   // e.g. "18K PVD Champagne Gold"
    private Integer stock;     // Available stock quantity in inventory
    private String status;     // "AVAILABLE", "SOLD"
}