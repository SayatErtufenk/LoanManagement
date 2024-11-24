package com.bank.loanmanagement.exception;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Eğer isterseniz, 'cause' parametresini de ekleyebilirsiniz
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
