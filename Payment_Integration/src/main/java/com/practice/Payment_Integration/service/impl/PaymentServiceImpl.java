package com.practice.Payment_Integration.service.impl;

import com.practice.Payment_Integration.dto.request.PaymentRequest;
import com.practice.Payment_Integration.dto.response.PaymentResponse;
import com.practice.Payment_Integration.entity.Order;
import com.practice.Payment_Integration.entity.Payment;
import com.practice.Payment_Integration.enums.OrderStatus;
import com.practice.Payment_Integration.enums.PaymentStatus;
import com.practice.Payment_Integration.exception.IdempotencyKeyConflictException;
import com.practice.Payment_Integration.exception.IdempotencyKeyMissingException;
import com.practice.Payment_Integration.exception.InvalidPaymentStateException;
import com.practice.Payment_Integration.exception.OrderNotFoundException;
import com.practice.Payment_Integration.repository.OrderRepository;
import com.practice.Payment_Integration.repository.PaymentRepository;
import com.practice.Payment_Integration.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentPersistenceService paymentPersistenceService;

    @Transactional
    @Override
    public PaymentResponse createPayment(
            PaymentRequest request,
            String idempotencyKey) {

        // 1. Validate idempotency key
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IdempotencyKeyMissingException(
                    "Idempotency-Key is required"
            );
        }

        // 2. Check whether this idempotency key was already used
        Payment existingPayment =
                paymentRepository.findByIdempotencyKey(idempotencyKey)
                        .orElse(null);

        if (existingPayment != null) {

            validateIdempotency(
                    existingPayment,
                    request.getOrderId()
            );

            // Same idempotency key + same order
            // Return the previously created payment
            return mapToResponse(existingPayment);
        }

        // 3. Find the order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: "
                                        + request.getOrderId()
                        ));

        // 4. Check whether the order is eligible for payment
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new InvalidPaymentStateException(
                    "Payment cannot be created for order with status: "
                            + order.getStatus()
            );
        }

        // 5. Create payment using trusted order data
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .amount(order.getTotalAmount())
                .status(PaymentStatus.CREATED)
                .idempotencyKey(idempotencyKey)
                .build();

        try {

            // 6. Persist payment
            Payment savedPayment =
                    paymentPersistenceService.save(payment);

            // 7. Return response
            return mapToResponse(savedPayment);

        } catch (DataIntegrityViolationException exception) {

            /*
             * Another request may have inserted the same
             * idempotency key between our initial lookup
             * and the save operation.
             *
             * The database unique constraint handles the race.
             */
            Payment existingPaymentAfterConflict =
                    paymentRepository
                            .findByIdempotencyKey(idempotencyKey)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Payment could not be created"
                                    ));

            validateIdempotency(
                    existingPaymentAfterConflict,
                    request.getOrderId()
            );

            // Return the payment created by the competing request
            return mapToResponse(existingPaymentAfterConflict);
        }
    }

    /**
     * Validates that an idempotency key is being reused
     * for the same order.
     */
    private void validateIdempotency(
            Payment payment,
            Long requestedOrderId) {

        if (!payment.getOrderId().equals(requestedOrderId)) {
            throw new IdempotencyKeyConflictException(
                    "Idempotency key was already used for another order"
            );
        }
    }

    /**
     * Maps Payment entity to API response.
     */
    private PaymentResponse mapToResponse(Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }
}