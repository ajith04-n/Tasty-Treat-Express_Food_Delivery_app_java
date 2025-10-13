package com.tastytreat.backend.tasty_treat_express_backend.exceptions;

public class OrderValidationException extends RuntimeException {
    public OrderValidationException(String message) {
        super(message);
    }
    
}
