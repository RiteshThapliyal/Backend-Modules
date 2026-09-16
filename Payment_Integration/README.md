# Payment Integration

A focused Spring Boot backend project for deeply understanding online payment integration and payment engineering using Razorpay first and Stripe later.

This is intentionally **not a full e-commerce application**.

The e-commerce domain is used only as a realistic business context so that payment concepts such as orders, payment attempts, verification, webhooks, refunds, idempotency, and failure handling can be implemented and understood in a practical way.

---

# Project Goal

The primary goal of this project is to deeply understand and implement payment integration in a Spring Boot backend.

The project will focus on:

- Understanding how online payments work
- Designing a payment-oriented backend architecture
- Integrating Razorpay
- Understanding payment verification
- Handling payment failures
- Handling webhooks
- Understanding idempotency
- Handling refunds
- Understanding payment states
- Designing reliable payment workflows
- Integrating Stripe after Razorpay
- Understanding production-grade payment architecture

The objective is not to complete as many e-commerce features as possible.

The objective is to build the minimum realistic business system required to understand payments deeply.

---

# Scope

## Included

The project will contain only the domain concepts required to support payment learning.

Initial domain:

```text
User
Product
Order
Payment