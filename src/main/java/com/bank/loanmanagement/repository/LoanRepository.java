package com.bank.loanmanagement.repository;

import com.bank.loanmanagement.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerId( Long customerId);
}
