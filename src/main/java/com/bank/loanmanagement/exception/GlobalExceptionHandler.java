package com.bank.loanmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.security.InvalidParameterException;

/**
 * @author Sayat Ertüfenk
 * @since 24/11/2024
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    // ResourceNotFoundException için
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> resourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // InvalidParameterException için
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<String> invalidParameterException(
            InvalidParameterException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // InsufficientCreditLimitException için
    @ExceptionHandler(InsufficientCreditLimitException.class)
    public ResponseEntity<String> insufficientCreditLimitException(
            InsufficientCreditLimitException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Genel istisnalar için
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> globalExceptionHandler(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
