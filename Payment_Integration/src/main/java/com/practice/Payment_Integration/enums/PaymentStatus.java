package com.practice.Payment_Integration.enums;

public enum PaymentStatus {

    CREATED("Payment created"),
    PENDING("Payment is pending"),
    SUCCESS("Payment successful"),
    FAILED("Payment failed"),
    REFUNDED("Payment refunded");

    private final String message;

    PaymentStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}