package com.project.SpringSecurity.common.response;

import java.time.LocalDateTime;

public final class ApiResponseBuilder {
    private ApiResponseBuilder () {};

    public static <T> ApiResponse<T> success (String message, T data)
    {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> success (String message)
    {
        return ApiResponse.<Void>builder()
                .success(true)
                .message(message)
                .data(null)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> failure (String message, T errors)
    {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(errors)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> failure (String message)
    {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .data(null)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
