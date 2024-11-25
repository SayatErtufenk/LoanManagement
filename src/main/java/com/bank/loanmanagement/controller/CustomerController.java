package com.bank.loanmanagement.controller;

import com.bank.loanmanagement.dto.CustomerDto;
import com.bank.loanmanagement.dto.LoanDto;
import com.bank.loanmanagement.exception.DuplicateUsernameException;
import com.bank.loanmanagement.exception.ResourceNotFoundException;
import com.bank.loanmanagement.model.Customer;
import com.bank.loanmanagement.model.Loan;
import com.bank.loanmanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fg
 *
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@RestController
@RequestMapping( "/customers" )
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // List all clients (allow only ADMIN role)
    @GetMapping
    @PreAuthorize( "hasRole('ADMIN')" )
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map( this::convertToDto ).collect( Collectors.toList() );
    }

    // Create a client (allow only ADMIN role)
    @PostMapping
    @PreAuthorize( "hasRole('ADMIN')" )
    public Customer createCustomer( @RequestBody Customer customer ) {

        Optional<Customer> existingCustomer = customerRepository.findByUsername( customer.getUsername() );
        if ( existingCustomer.isPresent() ) {
            throw new DuplicateUsernameException( "There is another record for the same username." );
        }

        String plainPassword = customer.getPassword();
        if ( plainPassword == null || plainPassword.isEmpty() ) {
            throw new IllegalArgumentException( "Password cannot be empty" );
        }
        String hashedPassword = passwordEncoder.encode( plainPassword );
        customer.setPassword( hashedPassword );

        if ( customer.getRole() == null || customer.getRole().isEmpty() ) {
            customer.setRole( "CUSTOMER" );
        }

        return customerRepository.save( customer );
    }

    // View a specific customer (allow only ADMIN role)
    @GetMapping( "/{id}" )
    @PreAuthorize( "hasRole('ADMIN')" )
    public Customer getCustomerById( @PathVariable Long id ) {
        return customerRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "No customer found" ) );
    }

    // Update client (allow only ADMIN role)
    @PutMapping( "/{id}" )
    @PreAuthorize( "hasRole('ADMIN')" )
    public Customer updateCustomer( @PathVariable Long id, @RequestBody Customer customerDetails ) {
        Customer customer = customerRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Customer not found" ) );

        customer.setName( customerDetails.getName() );
        customer.setSurname( customerDetails.getSurname() );
        customer.setUsername( customerDetails.getUsername() );
        customer.setCreditLimit( customerDetails.getCreditLimit() );
        customer.setUsedCreditLimit( customerDetails.getUsedCreditLimit() );

        return customerRepository.save( customer );
    }

    // Delete client (allow only ADMIN role)
    @DeleteMapping( "/{id}" )
    @PreAuthorize( "hasRole('ADMIN')" )
    public String deleteCustomer( @PathVariable Long id ) {
        Customer customer = customerRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Customer not found" ) );

        customerRepository.delete( customer );
        return "Customer deleted: " + id;
    }


    private CustomerDto convertToDto( Customer customer ) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId( customer.getId() );
        customerDto.setName( customer.getName() );
        customerDto.setSurname( customer.getSurname() );
        customerDto.setUsername( customer.getUsername() );
        customerDto.setCreditLimit( customer.getCreditLimit() );
        customerDto.setUsedCreditLimit( customer.getUsedCreditLimit() );

        // Converting loans
        List<LoanDto> loanDtos = customer.getLoans().stream().map( this::convertLoanToDto ).collect( Collectors.toList() );
        customerDto.setLoans( loanDtos );
        return customerDto;
    }

    private LoanDto convertLoanToDto( Loan loan ) {
        LoanDto loanDto = new LoanDto();
        loanDto.setId( loan.getId() );
        loanDto.setLoanAmount( loan.getLoanAmount() );
        loanDto.setTotalAmount( loan.getTotalAmount() );
        loanDto.setNumberOfInstallment( loan.getNumberOfInstallment() );
        loanDto.setCreateDate( loan.getCreateDate() );
        loanDto.setPaid( loan.isPaid() );

        return loanDto;
    }

}