package com.bank.loanmanagement.helpers;

import java.math.BigDecimal;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

public class AdjustmentResult {
    private BigDecimal adjustedAmount;
    private BigDecimal discount;
    private BigDecimal penalty;

    public AdjustmentResult(BigDecimal adjustedAmount, BigDecimal discount, BigDecimal penalty) {
        this.adjustedAmount = adjustedAmount;
        this.discount = discount;
        this.penalty = penalty;
    }

    public BigDecimal getAdjustedAmount() {
        return adjustedAmount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }
}
