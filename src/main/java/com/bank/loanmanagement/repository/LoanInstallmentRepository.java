package com.bank.loanmanagement.repository;

import com.bank.loanmanagement.model.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    List<LoanInstallment> findByLoanId( Long loanId );
}