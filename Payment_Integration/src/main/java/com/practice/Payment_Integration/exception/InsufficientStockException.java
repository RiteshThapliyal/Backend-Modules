package com.practice.Payment_Integration.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException (String message)
    {
        super (message);
    }
}
