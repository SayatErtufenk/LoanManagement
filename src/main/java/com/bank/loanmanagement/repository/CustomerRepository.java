package com.bank.loanmanagement.repository;

import com.bank.loanmanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNameAndSurname( String name, String surname);

    Optional<Customer> findByUsername(String username);
}
