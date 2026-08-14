package lk.ijse.jewellery_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "savings_transaction")
public class SavingsTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double paidAmount;
    private Double goldWeightGained; // සල්ලි වලින් හම්බුණු රන් බර
    private Double goldRateAtTime; // ඒ වෙලාවේ තිබුණු රන් මිල
    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "savings_account_id")
    private SavingsAccount savingsAccount;
}