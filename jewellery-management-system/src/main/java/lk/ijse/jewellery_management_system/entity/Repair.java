package lk.ijse.jewellery_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repair")
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String itemName;
    private String description;

    private String status;

    private LocalDate receivedDate;
    private LocalDate returnDate;
    private Double estimatedCost;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
