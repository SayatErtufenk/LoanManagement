package com.bank.loanmanagement.service;

import com.bank.loanmanagement.dto.LoanRequestDto;
import com.bank.loanmanagement.dto.LoanResponseDto;
import com.bank.loanmanagement.exception.InsufficientCreditLimitException;
import com.bank.loanmanagement.exception.InvalidParameterException;
import com.bank.loanmanagement.exception.ResourceNotFoundException;
import com.bank.loanmanagement.model.Customer;
import com.bank.loanmanagement.model.Loan;
import com.bank.loanmanagement.repository.CustomerRepository;
import com.bank.loanmanagement.repository.LoanInstallmentRepository;
import com.bank.loanmanagement.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Sayat Ertüfenk
 * @since 25/11/2024
 */

@SpringBootTest
class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository installmentRepository;

    @Test
    void testCreateLoan_Success() {
        // Arrange
        LoanRequestDto requestDto = new LoanRequestDto( 1L, new BigDecimal( "10000" ), new BigDecimal( "0.2" ), 12 );

        Customer customer = new Customer();
        customer.setId( 1L );
        customer.setCreditLimit( new BigDecimal( "50000" ) );
        customer.setUsedCreditLimit( new BigDecimal( "0" ) );

        when( customerRepository.findById( 1L ) ).thenReturn( Optional.of( customer ) );

        // Mock loanRepository.save() to return the loan with an assigned ID
        when( loanRepository.save( any( Loan.class ) ) ).thenAnswer( invocation -> {
            Loan savedLoan = invocation.getArgument( 0 );
            savedLoan.setId( 1L ); // Assign an ID to the loan
            return savedLoan;
        } );

        // Act
        LoanResponseDto responseDto = loanService.createLoan( requestDto );

        // Assert assertNotNull(responseDto);
        assertEquals( 1L, responseDto.getLoanId() ); // Verify that loanId is set
        assertEquals( 12, responseDto.getNumberOfInstallments() );
        assertEquals( new BigDecimal( "12000.0" ), responseDto.getTotalAmount() );
        verify( customerRepository, times( 1 ) ).save( customer );
        verify( loanRepository, times( 1 ) ).save( any( Loan.class ) );
        verify( installmentRepository, times( 1 ) ).saveAll( anyList() );
    }

    @Test
    void testCreateLoan_CustomerNotFound() {
        // Arrange
        LoanRequestDto requestDto = new LoanRequestDto( 1L, new BigDecimal( "10000" ), new BigDecimal( "0.2" ), 12 );

        when( customerRepository.findById( 1L ) ).thenReturn( Optional.empty() );

        // Act & Assert
        assertThrows( ResourceNotFoundException.class, () -> loanService.createLoan( requestDto ) );
    }

    @Test
    void testCreateLoan_InsufficientCreditLimit() {
        // Arrange
        LoanRequestDto requestDto = new LoanRequestDto( 1L, new BigDecimal( "10000" ), new BigDecimal( "0.2" ), 12 );

        Customer customer = new Customer();
        customer.setId( 1L );
        customer.setCreditLimit( new BigDecimal( "10000" ) );
        customer.setUsedCreditLimit( new BigDecimal( "5000" ) );

        when( customerRepository.findById( 1L ) ).thenReturn( Optional.of( customer ) );

        // Act & Assert
        assertThrows( InsufficientCreditLimitException.class, () -> loanService.createLoan( requestDto ) );
    }

    @Test
    void testCreateLoan_InvalidInterestRate() {
        // Arrange
        LoanRequestDto requestDto = new LoanRequestDto( 1L, new BigDecimal( "10000" ), new BigDecimal( "0.05" ), 12 );

        Customer customer = new Customer();
        customer.setId( 1L );
        customer.setCreditLimit( new BigDecimal( "10000" ) );
        customer.setUsedCreditLimit( new BigDecimal( "5000" ) );

        when( customerRepository.findById( 1L ) ).thenReturn( Optional.of( customer ) );

        // Act & Assert
        assertThrows( InvalidParameterException.class, () -> loanService.createLoan( requestDto ) );
    }

    @Test
    void testCreateLoan_InvalidInstallmentNumber() {
        // Arrange
        LoanRequestDto requestDto = new LoanRequestDto( 1L, new BigDecimal( "10000" ), new BigDecimal( "0.2" ), 10 );

        Customer customer = new Customer();
        customer.setId( 1L );
        customer.setCreditLimit( new BigDecimal( "10000" ) );
        customer.setUsedCreditLimit( new BigDecimal( "5000" ) );

        when( customerRepository.findById( 1L ) ).thenReturn( Optional.of( customer ) );

        // Act & Assert
        assertThrows( InvalidParameterException.class, () -> loanService.createLoan( requestDto ) );
    }
}
