package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {
    private Integer productId;
    private String productName;
    private String itemType;
    private String karat;
    private Integer qty;
    private Double unitPrice;
    private Double lineTotal;
}
