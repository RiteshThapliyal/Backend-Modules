package com.practice.Payment_Integration.service.impl;

import com.practice.Payment_Integration.entity.Payment;
import com.practice.Payment_Integration.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentPersistenceService {

    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Payment save(Payment payment) {
        return paymentRepository.saveAndFlush(payment);
    }
}