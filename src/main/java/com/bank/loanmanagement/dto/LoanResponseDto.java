package com.bank.loanmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponseDto {
    private Long loanId;
    private BigDecimal totalAmount;
    private BigDecimal loanAmount;
    private int numberOfInstallments;
    private boolean isPaid;
}

