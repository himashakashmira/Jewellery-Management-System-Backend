package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairStatsDTO {
    private Long totalRepairs;
    private Long readyCount;
    private Long craftingCount;
    private Double totalRevenue;
}
