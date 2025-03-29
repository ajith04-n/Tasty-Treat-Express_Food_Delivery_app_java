package com.tastytreat.backend.tasty_treat_express_backend.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
