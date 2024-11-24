package com.bank.loanmanagement.exception;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

public class InsufficientCreditLimitException extends RuntimeException {

    public InsufficientCreditLimitException(String message) {
        super(message);
    }

    public InsufficientCreditLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
