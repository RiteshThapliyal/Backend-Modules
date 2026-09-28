package com.practice.Payment_Integration.controller;

import com.practice.Payment_Integration.dto.request.PaymentRequest;
import com.practice.Payment_Integration.dto.response.ApiResponse;
import com.practice.Payment_Integration.dto.response.PaymentResponse;
import com.practice.Payment_Integration.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment (
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody PaymentRequest request)
    {
        PaymentResponse paymentResponse = paymentService.createPayment (request, idempotencyKey);

        ApiResponse<PaymentResponse> response = ApiResponse.<PaymentResponse>builder()
                    .status("SUCCESS")
                    .message("Payment created successfully")
                    .data(paymentResponse)
                    .timestamp(Instant.now())
                    .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
