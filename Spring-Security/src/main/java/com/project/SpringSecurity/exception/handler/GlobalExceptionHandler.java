package com.project.SpringSecurity.exception.handler;

import com.project.SpringSecurity.common.constants.ApiMessages;
import com.project.SpringSecurity.common.response.ApiResponse;
import com.project.SpringSecurity.common.response.ApiResponseBuilder;
import com.project.SpringSecurity.common.response.ErrorResponse;
import com.project.SpringSecurity.exception.registration.EmailAlreadyExistException;
import com.project.SpringSecurity.exception.registration.InvalidRefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleEmailAlreadyExists (EmailAlreadyExistException exception)
    {
        Map<String, String> errors = new HashMap<>();

        errors.put("email", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseBuilder.failure(
                        ApiMessages.REGISTRATION_FAILED,
                        errorResponse
                ));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidRefreshToken(
            InvalidRefreshTokenException exception) {

        Map<String, String> errors = new HashMap<>();

        errors.put("refreshToken", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseBuilder.failure(
                        "Refresh token validation failed",
                        errorResponse
                ));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errors(errors)
                .build();

        return ResponseEntity.badRequest()
                .body(ApiResponseBuilder.failure(
                        ApiMessages.VALIDATION_FAILED,
                        errorResponse
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException (Exception exception)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseBuilder.failure(ApiMessages.INTERNAL_SERVER_ERROR));
    }
}
