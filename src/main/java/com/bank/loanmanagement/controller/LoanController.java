package com.bank.loanmanagement.controller;

import com.bank.loanmanagement.dto.InstallmentDto;
import com.bank.loanmanagement.dto.LoanRequestDto;
import com.bank.loanmanagement.dto.LoanResponseDto;
import com.bank.loanmanagement.dto.PaymentResponseDto;
import com.bank.loanmanagement.exception.ResourceNotFoundException;
import com.bank.loanmanagement.model.Customer;
import com.bank.loanmanagement.repository.CustomerRepository;
import com.bank.loanmanagement.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@RestController
@RequestMapping( "/loans" )
public class LoanController {
    @Autowired
    private LoanService loanService;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public LoanResponseDto createLoan( @RequestBody LoanRequestDto request ) {
        return loanService.createLoan( request );
    }

    // Tüm kredileri listeleme - Sadece ADMIN rolüne izin ver
    @GetMapping( "/all" )
    @PreAuthorize( "hasRole('ADMIN')" )
    public List<LoanResponseDto> getAllLoans() {
        return loanService.getAllLoans();
    }

    // Müşterinin kendi kredilerini listeleme - CUSTOMER rolüne izin ver
    @GetMapping( "/my-loans" )
    @PreAuthorize( "hasRole('CUSTOMER')" )
    public List<LoanResponseDto> getMyLoans( Authentication authentication ) {
        String username = authentication.getName();
        Customer customer = customerRepository.findByUsername( username ).orElseThrow( () -> new ResourceNotFoundException( "Müşteri bulunamadı" ) );
        return loanService.listLoans( customer.getId() );
    }

    @GetMapping( "/customer/{customerId}" )
    public List<LoanResponseDto> getLoansByCustomer( @PathVariable Long customerId ) {
        return loanService.listLoans( customerId );
    }

    @GetMapping( "/{loanId}/installments" )
    public List<InstallmentDto> getInstallmentsByLoan( @PathVariable Long loanId ) {
        return loanService.listInstallments( loanId );
    }

    @PostMapping( "/{loanId}/pay" )
    public PaymentResponseDto payLoan( @PathVariable Long loanId, @RequestParam BigDecimal amount ) {
        return loanService.payLoan( loanId, amount );
    }
}