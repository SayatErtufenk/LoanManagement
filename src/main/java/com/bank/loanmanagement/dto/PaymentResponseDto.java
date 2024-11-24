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
public class PaymentResponseDto {
    private int numberOfInstallmentsPaid;
    private BigDecimal totalAmountPaid;
    private boolean isLoanFullyPaid;
    private BigDecimal totalDiscount;
    private BigDecimal totalPenalty;
}
