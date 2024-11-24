package com.bank.loanmanagement.service;

import com.bank.loanmanagement.dto.InstallmentDto;
import com.bank.loanmanagement.dto.LoanRequestDto;
import com.bank.loanmanagement.dto.LoanResponseDto;
import com.bank.loanmanagement.dto.PaymentResponseDto;
import com.bank.loanmanagement.exception.InsufficientCreditLimitException;
import com.bank.loanmanagement.exception.ResourceNotFoundException;
import com.bank.loanmanagement.model.Customer;
import com.bank.loanmanagement.model.Loan;
import com.bank.loanmanagement.model.LoanInstallment;
import com.bank.loanmanagement.repository.CustomerRepository;
import com.bank.loanmanagement.repository.LoanInstallmentRepository;
import com.bank.loanmanagement.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@Service
public class LoanService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanInstallmentRepository installmentRepository;

    @Autowired
    private PaymentService paymentService;


    public List<LoanResponseDto> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();

        // Kredileri DTO'ya dönüştür
        List<LoanResponseDto> loanDtos = loans.stream()
                .map( loan -> new LoanResponseDto(
                        loan.getId(),
                        loan.getTotalAmount(),
                        loan.getLoanAmount(),
                        loan.getNumberOfInstallment(),
                        loan.isPaid()
                )).collect( Collectors.toList() );
        return loanDtos;
    }

    public LoanResponseDto createLoan( LoanRequestDto request ) {
        // Müşteriyi doğrula
        Customer customer = customerRepository.findById( request.getCustomerId() )
                .orElseThrow( () -> new ResourceNotFoundException( "Müşteri bulunamadı" ) );

        // Taksit sayısı ve faiz oranı kontrolleri
        validateLoanRequest( request );

        // Toplam kredi tutarını hesapla
        BigDecimal totalLoanAmount = request.getAmount()
                .multiply( BigDecimal.ONE.add( request.getInterestRate() ) );

        // Müşterinin kredi limitiyle karşılaştır
        BigDecimal newUsedCreditLimit = customer.getUsedCreditLimit().add( totalLoanAmount );
        if ( newUsedCreditLimit.compareTo( customer.getCreditLimit() ) > 0 ) {
            throw new InsufficientCreditLimitException( "Yetersiz kredi limiti" );
        }

        // Kredi ve taksitlerin oluşturulması
        Loan loan = new Loan();
        loan.setCustomer( customer );
        loan.setLoanAmount( request.getAmount() );
        loan.setTotalAmount( totalLoanAmount );
        loan.setNumberOfInstallment( request.getNumberOfInstallment() );
        loan.setCreateDate( new Date() );
        loan.setPaid( false );

        loan = loanRepository.save( loan );

        // Taksit tutarını hesapla
        BigDecimal installmentAmount = totalLoanAmount.divide(
                BigDecimal.valueOf( request.getNumberOfInstallment() ), 2, BigDecimal.ROUND_HALF_UP );

        // Taksitleri oluştur
        List<LoanInstallment> installments = createInstallments( loan, installmentAmount, request.getNumberOfInstallment() );
        installmentRepository.saveAll( installments );

        // Müşterinin kullanılan kredi limitini güncelle
        customer.setUsedCreditLimit( newUsedCreditLimit );
        customerRepository.save( customer );

        // Yanıtın hazırlanması
        return new LoanResponseDto(
                loan.getId(),
                totalLoanAmount,
                installmentAmount,
                request.getNumberOfInstallment(),
                loan.isPaid()
        );
    }

    private void validateLoanRequest( LoanRequestDto request ) {
        List<Integer> validInstallments = Arrays.asList( 6, 9, 12, 24 );
        if ( !validInstallments.contains( request.getNumberOfInstallment() ) ) {
            throw new InvalidParameterException( "Geçersiz taksit sayısı" );
        }

        if ( request.getInterestRate().compareTo( new BigDecimal( "0.1" ) ) < 0 ||
                request.getInterestRate().compareTo( new BigDecimal( "0.5" ) ) > 0 ) {
            throw new InvalidParameterException( "Faiz oranı 0.1 ile 0.5 arasında olmalıdır" );
        }
    }

    private List<LoanInstallment> createInstallments( Loan loan, BigDecimal amount, int count ) {
        List<LoanInstallment> installments = new ArrayList<>();
        LocalDate dueDate = LocalDate.now().withDayOfMonth( 1 ).plusMonths( 1 );

        for ( int i = 0; i < count; i++ ) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan( loan );
            installment.setAmount( amount );
            installment.setDueDate( Date.from( dueDate.atStartOfDay( ZoneId.systemDefault() ).toInstant() ) );
            installment.setPaid( false );

            installments.add( installment );
            dueDate = dueDate.plusMonths( 1 );
        }
        return installments;
    }

    public List<LoanResponseDto> listLoans( Long customerId ) {
        // Müşterinin varlığını kontrol et
        Customer customer = customerRepository.findById( customerId )
                .orElseThrow( () -> new ResourceNotFoundException( "Müşteri bulunamadı" ) );

        // Müşteriye ait kredileri al
        List<Loan> loans = loanRepository.findByCustomerId( customerId );

        // Kredileri DTO'ya dönüştür
        List<LoanResponseDto> loanDtos = loans.stream()
                .map( loan -> new LoanResponseDto(
                        loan.getId(),
                        loan.getTotalAmount(),
                        loan.getLoanAmount(),
                        loan.getNumberOfInstallment(),
                        loan.isPaid()
                ) )
                .collect( Collectors.toList() );

        return loanDtos;
    }

    public List<InstallmentDto> listInstallments( Long loanId ) {
        // Kredinin varlığını kontrol et
        Loan loan = loanRepository.findById( loanId )
                .orElseThrow( () -> new ResourceNotFoundException( "Kredi bulunamadı" ) );

        // Krediye ait taksitleri al
        List<LoanInstallment> installments = installmentRepository.findByLoanId( loanId );

        // Taksitleri DTO'ya dönüştür
        List<InstallmentDto> installmentDtos = installments.stream()
                .map( installment -> new InstallmentDto(
                        installment.getId(),
                        installment.getAmount(),
                        installment.getPaidAmount(),
                        installment.getDueDate(),
                        installment.getPaymentDate(),
                        installment.isPaid()
                ) )
                .collect( Collectors.toList() );

        return installmentDtos;
    }

    public PaymentResponseDto payLoan( Long loanId, BigDecimal amount ) {
        // PaymentService aracılığıyla ödeme işlemini gerçekleştir
        return paymentService.payLoan( loanId, amount );
    }
}
