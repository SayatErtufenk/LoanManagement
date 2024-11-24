package com.bank.loanmanagement.exception;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

public class InvalidParameterException extends RuntimeException {

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
