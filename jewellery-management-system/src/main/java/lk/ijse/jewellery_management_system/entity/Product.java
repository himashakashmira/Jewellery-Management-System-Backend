package lk.ijse.jewellery_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private Double weight;
    private Double wastage;
    private Double labourCost;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
