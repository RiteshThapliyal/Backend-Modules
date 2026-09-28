package com.practice.Payment_Integration.dto.response;

import com.practice.Payment_Integration.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
}
