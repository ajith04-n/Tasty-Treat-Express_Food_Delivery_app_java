package com.tastytreat.backend.tasty_treat_express_backend.exceptions;

public class MainExceptionClass extends RuntimeException {

    public static class DatabaseConnectionException extends RuntimeException {
        public DatabaseConnectionException(String message) {
            super(message);
        }
    }

    public static class DuplicateResourceException extends RuntimeException {
        public DuplicateResourceException(String message) {
            super(message);
        }
    }

    public static class DatabaseOperationException extends RuntimeException {
        public DatabaseOperationException(String message) {
            super(message);
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    public static class PasswordUpdateFailedException extends RuntimeException {
        public PasswordUpdateFailedException(String message) {
            super(message);
        }
    }

    public static class NoActiveSessionException extends RuntimeException {
        public NoActiveSessionException(String message) {
            super(message);
        }
    }

    public static class InvalidEmailException extends RuntimeException {
        public InvalidEmailException(String message) {
            super(message);
        }
    }

    public static class InvalidPasswordException extends RuntimeException {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }

    public static class RestaurantNotFoundException extends RuntimeException {
        public RestaurantNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidCouponException extends RuntimeException {
        public InvalidCouponException(String message) {
            super(message);
        }
    }

    public static class InvalidPaymentMethodException extends RuntimeException {
        public InvalidPaymentMethodException(String message) {
            super(message);
        }
    }

    public static class InvalidOrderStatusException extends RuntimeException {
        public InvalidOrderStatusException(String message) {
            super(message);
        }
    }

    public static class EmailSendingException extends RuntimeException {
        public EmailSendingException(String message) {
            super(message);
        }
    }

    class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    class InvalidEnumValueException extends RuntimeException {
        public InvalidEnumValueException(String message) {
            super(message);
        }
    }

    class DuplicateMenuItemException extends RuntimeException {
        public DuplicateMenuItemException(String message) {
            super(message);
        }
    }

    class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String message) {
            super(message);
        }
    }

    class FeedbackNotFoundException extends RuntimeException {
        public FeedbackNotFoundException(String message) {
            super(message);
        }
    }

    class InvalidPhoneNumberException extends RuntimeException {
        public InvalidPhoneNumberException(String message) {
            super(message);
        }
    }

}
