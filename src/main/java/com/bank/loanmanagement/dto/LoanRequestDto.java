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
public class LoanRequestDto {
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private int numberOfInstallment;
}