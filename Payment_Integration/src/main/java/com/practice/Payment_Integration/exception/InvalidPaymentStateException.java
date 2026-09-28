package com.practice.Payment_Integration.exception;

public class InvalidPaymentStateException extends RuntimeException {

    public InvalidPaymentStateException(String message) {
        super(message);
    }
}