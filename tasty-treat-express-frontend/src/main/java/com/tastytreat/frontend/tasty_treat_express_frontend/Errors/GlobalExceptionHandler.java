package com.tastytreat.frontend.tasty_treat_express_frontend.Errors;

import com.tastytreat.frontend.tasty_treat_express_frontend.Errors.MainExceptionClass.*;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Something went wrong. Please try again later.");
        model.addAttribute("errorDetails", ex.getMessage());
        return "error"; 
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "User not found.");
        model.addAttribute("errorDetails", ex.getMessage());
        return "error"; 
    }
}
