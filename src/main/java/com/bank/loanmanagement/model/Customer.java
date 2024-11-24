package com.bank.loanmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;
    private String username;
    private String name;
    private String surname;
    private BigDecimal creditLimit;
    private BigDecimal usedCreditLimit = BigDecimal.ZERO;

    // Müşterinin kredileriyle ilişkilendirilmesi
    @OneToMany( mappedBy = "customer", cascade = CascadeType.ALL )
    private List<Loan> loans;
}