package com.project.SpringSecurity.exception.registration;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException (String message)
    {
        super(message);
    }
}
