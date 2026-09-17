package com.practice.Payment_Integration.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException (String message)
    {
        super (message);
    }
}
