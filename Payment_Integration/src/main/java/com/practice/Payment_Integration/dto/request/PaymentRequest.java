package com.practice.Payment_Integration.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Order ID is Required")
    private Long orderId;

}
