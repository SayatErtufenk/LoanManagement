package com.bank.loanmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Sayat Ertüfenk
 * @since 25/11/2024
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
    private Long id;
    private BigDecimal loanAmount;
    private BigDecimal totalAmount;
    private int numberOfInstallment;
    private Date createDate;
    private boolean isPaid;
}
