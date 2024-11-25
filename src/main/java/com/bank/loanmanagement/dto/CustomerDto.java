package com.bank.loanmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Sayat Ertüfenk
 * @since 25/11/2024
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private Long id;
    private String name;
    private String surname;
    private String username;
    private BigDecimal creditLimit;
    private BigDecimal usedCreditLimit;
    @ToString.Exclude
    private List<LoanDto> loans;
}