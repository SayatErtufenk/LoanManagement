package com.bank.loanmanagement.controller;

import com.bank.loanmanagement.dto.PaymentResponseDto;
import com.bank.loanmanagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@RestController
@RequestMapping( "/payments" )
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping( "/loan/{loanId}" )
    public PaymentResponseDto payLoan( @PathVariable Long loanId, @RequestParam BigDecimal amount ) {
        return paymentService.payLoan( loanId, amount );
    }
}