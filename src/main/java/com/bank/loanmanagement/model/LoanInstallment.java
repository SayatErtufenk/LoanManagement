package com.bank.loanmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount; // Taksit tutarı
    private BigDecimal paidAmount = BigDecimal.ZERO; // Ödenen tutar
    private Date dueDate; // Vade tarihi
    private Date paymentDate; // Ödeme tarihi
    private boolean isPaid = false;

    @ManyToOne
    private Loan loan;
}

