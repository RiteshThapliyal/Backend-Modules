package com.practice.Payment_Integration.exception;

public class IdempotencyKeyMissingException extends RuntimeException {

    public IdempotencyKeyMissingException(String message) {
        super(message);
    }
}