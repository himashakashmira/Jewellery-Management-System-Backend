package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private Integer id;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private Double discount;
    private Integer customerId;
    private String customerName;
    private String customerContact;
    private String orderType;
    private String orderRef;
    private String deliveryAddress;
    private String paymentMethod;
    private String status;
    private Integer totalItems;
    private List<OrderItemResponseDTO> items;
}
