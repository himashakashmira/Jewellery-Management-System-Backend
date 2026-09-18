package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoldRateDTO {
    private Double rate22K;
    private Double rate24K;
    private LocalDateTime updatedAt;
}