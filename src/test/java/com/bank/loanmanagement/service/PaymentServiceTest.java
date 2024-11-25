package com.bank.loanmanagement.service;

import com.bank.loanmanagement.dto.PaymentResponseDto;
import com.bank.loanmanagement.exception.ResourceNotFoundException;
import com.bank.loanmanagement.model.*;
import com.bank.loanmanagement.repository.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * @author Sayat Ertüfenk
 * @since 25/11/2024
 */

@SpringBootTest
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository installmentRepository;

    @Test
    void testPayLoan_Success_EarlyPayment() {
        // Arrange
        Long loanId = 1L;
        BigDecimal amount = new BigDecimal("1980");

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setPaid(false);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setAmount(new BigDecimal("1000"));
        installment1.setDueDate(Date.from( LocalDate.now().plusDays(10).atStartOfDay( ZoneId.systemDefault()).toInstant()));
        installment1.setPaid(false);

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setId(2L);
        installment2.setAmount(new BigDecimal("1000"));
        installment2.setDueDate(Date.from(LocalDate.now().plusDays(40).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        installment2.setPaid(false);

        List<LoanInstallment> installments = Arrays.asList(installment1, installment2);

        when(loanRepository.findById(loanId)).thenReturn( Optional.of(loan));
        when(installmentRepository.findByLoanId(loanId)).thenReturn(installments);

        // Act
        PaymentResponseDto response = paymentService.payLoan(loanId, amount);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getNumberOfInstallmentsPaid());
        assertEquals(new BigDecimal("1950.00"), response.getTotalAmountPaid());
        assertFalse(!response.isLoanFullyPaid());
    }

    @Test
    void testPayLoan_LoanNotFound() {
        // Arrange
        Long loanId = 1L;
        BigDecimal amount = new BigDecimal("1000");

        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows( ResourceNotFoundException.class, () -> paymentService.payLoan(loanId, amount));
    }
}
