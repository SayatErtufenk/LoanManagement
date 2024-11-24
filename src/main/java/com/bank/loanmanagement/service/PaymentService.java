package com.bank.loanmanagement.service;

import com.bank.loanmanagement.dto.PaymentResponseDto;
import com.bank.loanmanagement.exception.ResourceNotFoundException;
import com.bank.loanmanagement.helpers.AdjustmentResult;
import com.bank.loanmanagement.model.Loan;
import com.bank.loanmanagement.model.LoanInstallment;
import com.bank.loanmanagement.repository.LoanInstallmentRepository;
import com.bank.loanmanagement.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@Service
public class PaymentService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanInstallmentRepository installmentRepository;

    public PaymentResponseDto payLoan( Long loanId, BigDecimal amount ) {
        Loan loan = loanRepository.findById( loanId )
                .orElseThrow( () -> new ResourceNotFoundException( "Kredi bulunamadı" ) );

        List<LoanInstallment> installments = installmentRepository.findByLoanId( loanId );
        installments.sort( Comparator.comparing( LoanInstallment::getDueDate ) );

        int installmentsPaid = 0;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalPenalty = BigDecimal.ZERO;
        LocalDate currentDate = LocalDate.now();
        LocalDate maxPayableDate = currentDate.plusMonths( 3 ).withDayOfMonth( 1 );

        for ( LoanInstallment installment : installments ) {
            if ( installment.isPaid() ) continue;
            LocalDate dueDate = Instant.ofEpochMilli( installment.getDueDate().getTime() )
                    .atZone( ZoneId.systemDefault() ).toLocalDate();

            // Vade tarihi 3 aydan fazla gelecekte olan taksitler ödenemez
            if ( dueDate.isAfter( maxPayableDate ) ) break;

            // Ödül ve Ceza Hesaplaması
            AdjustmentResult adjustmentResult = calculateAdjustedAmount( installment, currentDate );

            BigDecimal adjustedAmount = adjustmentResult.getAdjustedAmount();
            BigDecimal discount = adjustmentResult.getDiscount();
            BigDecimal penalty = adjustmentResult.getPenalty();

            if ( amount.compareTo( adjustedAmount ) >= 0 ) {
                // Ödeme işlemi
                installment.setPaid( true );
                installment.setPaymentDate( Date.from( currentDate.atStartOfDay( ZoneId.systemDefault() ).toInstant() ) );
                installment.setPaidAmount( adjustedAmount );
                installmentRepository.save( installment );

                amount = amount.subtract( adjustedAmount );
                totalPaid = totalPaid.add( adjustedAmount );
                installmentsPaid++;

                // Toplam indirim ve ceza miktarlarını güncelle
                totalDiscount = totalDiscount.add( discount );
                totalPenalty = totalPenalty.add( penalty );
            } else {
                break;
            }
        }

        // Kredinin tamamen ödenip ödenmediğini kontrol et
        boolean isLoanFullyPaid = installments.stream().allMatch( LoanInstallment::isPaid );
        loan.setPaid( isLoanFullyPaid );
        loanRepository.save( loan );

        return new PaymentResponseDto( installmentsPaid, totalPaid, isLoanFullyPaid, totalDiscount, totalPenalty );
    }

    private AdjustmentResult calculateAdjustedAmount( LoanInstallment installment, LocalDate paymentDate ) {
        LocalDate dueDate = Instant.ofEpochMilli( installment.getDueDate().getTime() )
                .atZone( ZoneId.systemDefault() ).toLocalDate();

        BigDecimal originalAmount = installment.getAmount();
        BigDecimal adjustedAmount = originalAmount;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal penalty = BigDecimal.ZERO;

        long daysDifference = ChronoUnit.DAYS.between( paymentDate, dueDate );

        if ( daysDifference > 0 ) {
            // Erken ödeme indirimi
            discount = originalAmount
                    .multiply( BigDecimal.valueOf( 0.001 ) )
                    .multiply( BigDecimal.valueOf( daysDifference ) );

            adjustedAmount = originalAmount.subtract( discount );
        } else if ( daysDifference < 0 ) {
            // Gecikme cezası
            long lateDays = -daysDifference;
            penalty = originalAmount
                    .multiply( BigDecimal.valueOf( 0.001 ) )
                    .multiply( BigDecimal.valueOf( lateDays ) );

            adjustedAmount = originalAmount.add( penalty );
        }

        adjustedAmount = adjustedAmount.setScale( 2, BigDecimal.ROUND_HALF_UP );
        discount = discount.setScale( 2, BigDecimal.ROUND_HALF_UP );
        penalty = penalty.setScale( 2, BigDecimal.ROUND_HALF_UP );

        return new AdjustmentResult( adjustedAmount, discount, penalty );
    }

}
