package com.bank.loanmanagement.exception;

/**
 * @author Sayat Ertüfenk
 * @since 25/11/2024
 */

public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException( String message ) {
        super( message );
    }
}