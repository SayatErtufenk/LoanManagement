package com.bank.loanmanagement.helpers;

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
public class AdjustmentResult {
    private BigDecimal adjustedAmount;
    private BigDecimal discount;
    private BigDecimal penalty;
}
