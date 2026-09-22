package lk.ijse.jewellery_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportSummaryDTO {
    private Double grossRevenue;
    private Long totalOrders;
    private Double averageOrderValue;
    private Long totalItemsSold;
    private Double goldRevenue;
    private Double imitationRevenue;
    private Long totalImitationOrders;
    private Long pendingImitationOrders;
    private Long approvedImitationOrders;
    private List<OrderResponseDTO> recentOrders;
}
