package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Integer customerId;
    private Double discount;
    private String orderType; // "IMITATION", "GOLD", "POS"
    private String orderRef;
    private String deliveryAddress;
    private String paymentMethod;
    private String customerName;
    private String customerContact;
    private String status; // e.g. "PENDING_APPROVAL" or "APPROVED"
    private List<OrderDetailDTO> items; // list of items in the bill
}