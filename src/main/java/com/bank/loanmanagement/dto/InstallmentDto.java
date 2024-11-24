package com.bank.loanmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentDto {
    private Long id;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private Date dueDate;
    private Date paymentDate;
    private boolean isPaid;
}
