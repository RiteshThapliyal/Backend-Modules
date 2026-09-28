package com.practice.Payment_Integration.exception;

public class IdempotencyKeyConflictException extends RuntimeException {

    public IdempotencyKeyConflictException(String message) {
        super(message);
    }
}