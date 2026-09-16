package com.practice.Payment_Integration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private String message;
    private List<String> errors;
    private Instant timestamp;
}
