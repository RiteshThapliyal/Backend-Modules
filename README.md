# Spring Boot Backend Modules

A collection of Java Spring Boot backend modules focused on implementing
real-world backend features in a secure, scalable, and loosely coupled way.

The purpose of this repository is to build individual backend features
independently so they can be understood, tested, and integrated into
different applications based on specific business requirements.

---

## Modules

| Module | Description | Status |
|--------|-------------|--------|
| [Spring Security](./spring-security) | JWT authentication, authorization, refresh tokens and logout | 🚧 In Progress |
| [Payment Integration](./payment-integration) | Payment gateway integration and payment lifecycle | ⏳ Planned |
| [Email Service](./email-service) | Email sending and email-based workflows | ⏳ Planned |
| [File Upload](./file-upload) | File upload and storage handling | ⏳ Planned |

---

## How to Use This Repository

Each module is designed to be understood and integrated independently.

Choose the feature you want to implement and open its documentation.

For example:

**Need authentication?**

→ [Spring Security](./spring-security)

**Need payment integration?**

→ [Payment Integration](./payment-integration)

**Need email functionality?**

→ [Email Service](./email-service)

---

## Design Philosophy

This repository focuses on practical backend engineering and
business-driven trade-offs.

Some features may have multiple possible implementations.
The implementation chosen in each module is based on factors such as:

- Security
- Performance
- Complexity
- Scalability
- Maintainability
- Business requirements

The goal is not to claim that there is one "perfect" implementation,
but to understand the trade-offs involved in different architectural decisions.
