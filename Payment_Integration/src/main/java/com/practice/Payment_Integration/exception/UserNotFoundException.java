package com.practice.Payment_Integration.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException (String message)
    {
        super(message);
    }
}
