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
    private List<OrderDetailDTO> items; // list of items in the bill
}