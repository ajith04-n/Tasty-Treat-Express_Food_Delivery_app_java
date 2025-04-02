package com.tastytreat.backend.tasty_treat_express_backend.exceptions;

public class DuplicateMenuItemException extends RuntimeException {
    public DuplicateMenuItemException(String message) {
        super(message);
    }
    
}
