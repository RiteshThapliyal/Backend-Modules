package com.practice.Payment_Integration.service;

import com.practice.Payment_Integration.dto.request.PaymentRequest;
import com.practice.Payment_Integration.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment (PaymentRequest request, String idempotencyKey);
}
