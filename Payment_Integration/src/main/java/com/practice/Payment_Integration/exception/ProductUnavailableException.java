package com.practice.Payment_Integration.exception;

public class ProductUnavailableException extends RuntimeException {

    public ProductUnavailableException (String message)
    {
        super (message);
    }
}
