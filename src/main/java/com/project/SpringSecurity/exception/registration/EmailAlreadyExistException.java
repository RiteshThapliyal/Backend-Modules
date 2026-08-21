package com.project.SpringSecurity.exception.registration;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException (String message)
    {
        super (message);
    }
}
