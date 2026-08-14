package lk.ijse.jewellery_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "savings_account")
public class SavingsAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double totalGoldBalanceGrams; // Total Gold Weight
    private Double totalCashPaid;
    private LocalDate startDate;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
