package com.bank.loanmanagement.controller;

import com.bank.loanmanagement.exception.ResourceNotFoundException;
import com.bank.loanmanagement.model.Customer;
import com.bank.loanmanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    // Tüm müşterileri listeleme (sadece ADMIN rolüne izin ver)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Müşteri oluşturma (sadece ADMIN rolüne izin ver)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    // Belirli bir müşteriyi görüntüleme (sadece ADMIN rolüne izin ver)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri bulunamadı"));
    }

    // Müşteri güncelleme (sadece ADMIN rolüne izin ver)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Customer updateCustomer( @PathVariable Long id, @RequestBody Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri bulunamadı"));

        customer.setName(customerDetails.getName());
        customer.setSurname(customerDetails.getSurname());
        customer.setUsername(customerDetails.getUsername());
        customer.setCreditLimit(customerDetails.getCreditLimit());
        customer.setUsedCreditLimit( customerDetails.getUsedCreditLimit() );

        return customerRepository.save(customer);
    }

    // Müşteri silme (sadece ADMIN rolüne izin ver)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCustomer(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Müşteri bulunamadı"));

        customerRepository.delete(customer);
        return "Müşteri silindi: " + id;
    }
}
