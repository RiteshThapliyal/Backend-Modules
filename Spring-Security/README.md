# Spring Security

A practical Spring Boot implementation of authentication and authorization using
Spring Security, JWT access tokens, refresh tokens, password hashing, validation,
exception handling, and logout.

This module is designed as an independent backend feature that can be understood
and integrated into other Spring Boot applications.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Implementation](#implementation)
- [API Endpoints](#api-endpoints)
- [Security Decisions](#security-decisions)
- [Trade-offs](#trade-offs)
- [Testing](#testing)
- [Integration Guide](#integration-guide)

---

## Overview

This module implements a stateless authentication system using Spring Security
and JSON Web Tokens (JWT).

The authentication system uses two types of tokens:

- **Access Token** — short-lived token used to access protected APIs.
- **Refresh Token** — long-lived token used to generate a new access token after
  the access token expires.

The main goal of this module is not only to make authentication work, but also
to handle security-related edge cases and document the design decisions and
trade-offs involved in building the system.

### Authentication Flow

```text
                    Registration
                         │
                         ▼
                       User
                         │
                         ▼
                    Password Hash
                         │
                         ▼
                     Database


                       Login
                         │
                         ▼
                  Verify Credentials
                         │
                         ▼
              ┌──────────┴──────────┐
              ▼                     ▼
        Access Token          Refresh Token
         15 minutes              30 days
              │                     │
              ▼                     │
       Protected APIs               │
                                    │
                         Access Token expires
                                    │
                                    ▼
                              /auth/refresh
                                    │
                                    ▼
                           Validate Refresh Token
                                    │
                                    ▼
                          Generate New Access Token

```
## Features

Authentication

  User registration
  User login
  Credential validation
  Password hashing using BCrypt
  JWT access-token generation
  Refresh-token based authentication

Authorization

  Protected API endpoints
  Role-based authorization
  Authentication handling for unauthenticated requests
  Access-denied handling for authenticated users without permission

JWT

  JWT access-token generation
  JWT signature validation
  JWT authentication filter
  Extraction of user identity from JWT
  Stateless authentication for protected APIs
  Configurable access-token expiration

Refresh Tokens

  Refresh-token generation
  Secure storage using a hashed refresh token
  Refresh-token validation
  Refresh-token expiration checking
  Refresh-token revocation
  New access-token generation using a valid refresh token
  Configurable refresh-token expiration

Logout

  Logout endpoint
  Refresh-token revocation during logout
  Existing access token is allowed to expire naturally

Validation & Exception Handling
  Request validation using Jakarta Bean Validation
  Global exception handling
  Duplicate email handling
  Invalid refresh-token handling
  Validation error responses
  Consistent API response structure

OAuth2

  OAuth2 integration is the next feature being implemented in this module.

  The purpose of adding OAuth2 is to extend the authentication system with
  third-party identity providers and understand how OAuth2-based authentication
  fits alongside the existing JWT-based security architecture.

---

## Architecture

The module follows a layered architecture to keep responsibilities separated
and reduce coupling between different parts of the application.

                    Controller
                        │
                        ▼
                     Service
                        │
              ┌─────────┴─────────┐
              ▼                   ▼
        Repository          Security Services
              │                   │
              ▼                   ▼
           Database          JWT / Password
           

Controller
    │
    ├── AuthController
    │
    └── Handles authentication-related HTTP requests


Service
    │
    ├── AuthService
    │
    └── RefreshTokenService
    │
    └── Contains authentication and refresh-token business logic


Repository
    │
    ├── UserRepository
    │
    └── RefreshTokenRepository
    │
    └── Handles database access


Security
    │
    ├── Security Configuration
    ├── JwtAuthenticationFilter
    └── JwtService


DTO
    │
    ├── RegisterRequest
    ├── RefreshTokenRequest
    └── LoginResponse


Exception
    │
    ├── Custom Exceptions
    └── GlobalExceptionHandler

Request Authentication Flow

For a protected API request:

Client
  │
  │ Authorization: Bearer <JWT>
  ▼
JwtAuthenticationFilter
  │
  ▼
Extract JWT
  │
  ▼
Validate JWT
  │
  ▼
Extract User Identity
  │
  ▼
Load UserDetails
  │
  ▼
Create Authentication
  │
  ▼
SecurityContext
  │
  ▼
Protected Controller

The access token is validated before the request reaches the protected
controller.

## Implementation

The implementation is divided into separate components so that authentication, token management, security configuration, validation, and exception handling remain independent.

### User Registration

The registration flow creates a new user after validating the incoming request.

The main steps are:

1. Validate the registration request.
2. Check whether the email is already registered.
3. Hash the password using BCrypt.
4. Create the user entity.
5. Store the user in the database.
6. Return a consistent API response.

Passwords are never stored in plain text.

```text
RegisterRequest
      │
      ▼
Request Validation
      │
      ▼
Check Existing Email
      │
      ▼
Hash Password
      │
      ▼
Create User
      │
      ▼
Save User
      │
      ▼
RegisterResponse
```

### Login

The login process verifies the user's credentials and generates both authentication tokens.

```text
Login Request
      │
      ▼
Load User
      │
      ▼
Verify Password
      │
      ▼
Generate Access Token
      │
      ├──────────────► Access Token
      │                  15 minutes
      │
      ▼
Generate Refresh Token
                         │
                         ▼
                  Hash Refresh Token
                         │
                         ▼
                  Store Token Hash
```

The access token is returned to the client for accessing protected APIs.

The refresh token is stored in hashed form in the database and is used to obtain a new access token after the access token expires.

### Password Hashing

Passwords are hashed using BCrypt before being persisted.

The application never stores or compares raw passwords directly with database values.

```java
passwordEncoder.encode(password);
```

During authentication, Spring Security verifies the supplied password against the stored BCrypt hash.

### JWT Access Token

The access token contains the authenticated user's identity and is digitally signed using the configured JWT secret.

The token is validated by the security filter before allowing access to protected endpoints.

The access token is intentionally short-lived:

```text
Access Token Expiration
15 minutes
```

This limits the amount of time an exposed access token can be used.

### JWT Authentication Filter

The `JwtAuthenticationFilter` extends `OncePerRequestFilter`.

For each incoming request, the filter:

1. Checks the `Authorization` header.
2. Extracts the Bearer token.
3. Validates the JWT signature.
4. Extracts the user's identity.
5. Loads the corresponding user details.
6. Creates an `Authentication` object.
7. Stores the authentication inside the `SecurityContext`.

```text
HTTP Request
     │
     ▼
Authorization Header
     │
     ▼
Bearer Token
     │
     ▼
JWT Validation
     │
     ▼
User Identity
     │
     ▼
UserDetailsService
     │
     ▼
Authentication
     │
     ▼
SecurityContext
     │
     ▼
Controller
```

If the token is missing or invalid, the request does not become authenticated.

### Refresh Token

Refresh tokens are generated using cryptographically secure random bytes.

The raw refresh token is not stored directly in the database.

Instead:

```text
Raw Refresh Token
       │
       ▼
Hash
       │
       ▼
Database
```

The database stores the hash of the refresh token.

When the client requests a new access token, the supplied refresh token is verified against the stored hash.

The refresh token also contains server-side state such as:

* Expiration time
* Revocation status
* Associated user
* Creation time

### Refresh Token Validation

A refresh token is considered valid only when:

* The token exists.
* The token is not revoked.
* The token has not expired.
* The associated user exists.
* The supplied token matches the stored hash.

```text
Refresh Token
      │
      ▼
Find Stored Token
      │
      ▼
Verify Hash
      │
      ▼
Check Revoked
      │
      ▼
Check Expiration
      │
      ▼
Validate User
      │
      ▼
Generate New Access Token
```

Invalid refresh tokens result in an authentication failure rather than generating a new access token.

### Logout

Logout is implemented as a protected endpoint.

The client must provide a valid access token to access the logout operation.

During logout:

1. The authenticated request reaches the logout endpoint.
2. The associated refresh token is identified.
3. The refresh token is revoked.
4. The existing access token remains valid until it expires naturally.

```text
Logout Request
      │
      ▼
Valid Access Token
      │
      ▼
Identify Refresh Token
      │
      ▼
Revoke Refresh Token
      │
      ▼
Logout Successful
```

Because access tokens are stateless, they are not stored in the database and are not actively invalidated during logout.

The short access-token lifetime limits the period in which an already-issued access token can remain usable.

### Role-Based Authorization

The security configuration supports role-based authorization for protected endpoints.

Authentication answers:

```text
"Who is the user?"
```

Authorization answers:

```text
"Is this user allowed to perform this operation?"
```

A user can therefore be successfully authenticated while still receiving an access-denied response when attempting to access an endpoint that requires a different role.

### Validation

Request DTOs use Jakarta Bean Validation to reject invalid input before it reaches the business logic.

Validation is applied to fields such as:

* Email
* Password
* Required request values
* Refresh token values

Invalid requests are converted into a consistent API response through the global exception handler.

### Exception Handling

The module uses centralized exception handling instead of returning different response structures from individual controllers.

The `GlobalExceptionHandler` handles application-specific exceptions such as:

* Duplicate email
* Invalid refresh token
* Validation failures
* Authentication-related errors
* Other application exceptions

The objective is to keep controller code focused on HTTP request handling while keeping error processing centralized.

---

## API Endpoints

The authentication module exposes endpoints for registration, login, token refresh, and logout.

| Method | Endpoint         | Authentication | Description                                             |
| ------ | ---------------- | -------------- | ------------------------------------------------------- |
| POST   | `/auth/register` | Public         | Register a new user                                     |
| POST   | `/auth/login`    | Public         | Authenticate user and generate tokens                   |
| POST   | `/auth/refresh`  | Public         | Generate a new access token using a valid refresh token |
| POST   | `/auth/logout`   | Required       | Revoke the refresh token                                |

### Register

```http
POST /auth/register
Content-Type: application/json
```

Example request:

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123"
}
```

The password is hashed before the user is stored.

### Login

```http
POST /auth/login
Content-Type: application/json
```

Example request:

```json
{
  "email": "user@example.com",
  "password": "StrongPassword123"
}
```

A successful login generates:

```text
Access Token
Refresh Token
```

The access token is used for protected API requests.

### Refresh

```http
POST /auth/refresh
Content-Type: application/json
```

Example request:

```json
{
  "refreshToken": "<refresh-token>"
}
```

A valid refresh token results in a newly generated access token.

The refresh token must not be expired or revoked.

### Logout

```http
POST /auth/logout
Authorization: Bearer <access-token>
```

The logout operation revokes the refresh token associated with the authenticated user.

---

## Security Decisions

The following decisions were made intentionally while implementing the authentication system.

### Stateless Access-Token Authentication

The application uses JWT access tokens instead of maintaining server-side sessions.

This allows protected API requests to carry their authentication information without requiring an active server-side session.

### Short-Lived Access Tokens

The access token lifetime is configured to:

```text
15 minutes
```

A short lifetime reduces the window of opportunity if an access token is exposed.

### Long-Lived Refresh Tokens

The refresh token lifetime is configured to:

```text
30 days
```

Refresh tokens allow the client to obtain new access tokens without requiring the user to log in again after every access-token expiration.

### Hashed Refresh Tokens

Raw refresh tokens are not stored in the database.

Instead:

```text
Refresh Token
      │
      ▼
Hash
      │
      ▼
Database
```

This provides an additional layer of protection if the refresh-token database is exposed.

### Refresh Token Revocation

Refresh tokens contain a revocation state.

A revoked refresh token cannot be used to generate another access token.

This makes logout meaningful even though the access-token system itself is stateless.

### BCrypt Password Hashing

User passwords are stored using BCrypt rather than plain text.

The application never stores the original password.

### Protected Logout Endpoint

Logout requires authentication because the operation belongs to an authenticated user.

The access token identifies the authenticated user whose refresh token should be revoked.

### Centralized Error Handling

Authentication, authorization, validation, and application errors are converted into a consistent API response structure.

This keeps the API predictable for clients.

---

## Trade-offs

Logout & Token Revocation — Security vs. Complexity Trade-off

I am making several trade-offs in this project based on business requirements. One of the important trade-offs is between security and system complexity.
When implementing the logout endpoint, revoking the refresh token is important because it prevents the client from generating new access tokens after logout.
However, the access token has a short lifetime of 15 minutes. If I also want to invalidate the access token immediately during logout, I would need additional server-side state or a token blacklist/revocation mechanism. This would add complexity and introduce additional storage and lookup operations.

For this project, I have decided to make the following trade-off:
Revoke the refresh token immediately during logout. Allow the existing access token to remain valid until it expires. The access token has a short lifetime of 15 minutes, which limits the window of exposure after logout.
This means that after logout, an already-issued access token may technically remain usable for its remaining lifetime. However, the user cannot use the revoked refresh token to obtain a new access token.
This is a deliberate decision to balance security, performance, and implementation complexity based on the current business requirements.

Security decision: Refresh token is revoked on logout; access token is allowed to expire naturally.

The security architecture involves several intentional trade-offs.

### JWT vs Server-Side Sessions

JWT authentication removes the need to maintain an active authentication session on the server.

However, JWT access tokens cannot be easily revoked once issued.

The system therefore uses short-lived access tokens combined with revocable refresh tokens.

```text
JWT
+
Short Expiration
+
Refresh Token Revocation
```

This provides a balance between stateless authentication and logout/revocation requirements.

### Refresh Token Storage

Storing refresh tokens in hashed form provides better protection than storing raw tokens.

The trade-off is that refresh-token verification requires comparing the supplied token against the stored hash.

### Access Token Revocation

The system does not maintain a blacklist for every access token.

Instead, access tokens are allowed to expire naturally.

The trade-off is that an already-issued access token may remain usable until its expiration time even after logout.

The configured 15-minute lifetime limits this window.

### Database Lookup

JWT validation itself does not require a database lookup to verify the token signature.

However, the authentication filter can load user details to establish the user's security context.

This introduces database work for protected requests but allows the application to work with the current user state and authorization information.

---

## Testing

Security functionality should not be considered complete only because the happy path works.

The module should also be tested against invalid input, malformed tokens, expired tokens, revoked tokens, missing authentication, and authorization failures.

### Authentication Tests

The following cases should be tested:

* Successful registration
* Duplicate email registration
* Invalid registration data
* Successful login
* Incorrect password
* Non-existent email
* Missing credentials
* Invalid credentials
* Password hashing verification

### JWT Tests

Test cases should include:

* Valid access token
* Missing access token
* Malformed JWT
* Invalid JWT signature
* Expired access token
* Empty Bearer token
* Incorrect Authorization header
* Modified JWT payload
* Modified JWT signature

### Refresh Token Tests

Test cases should include:

* Valid refresh token
* Invalid refresh token
* Expired refresh token
* Revoked refresh token
* Refresh token belonging to another user
* Missing refresh token
* Empty refresh token
* Malformed refresh token

### Authorization Tests

Test cases should include:

* Authenticated user accessing a protected endpoint
* Unauthenticated user accessing a protected endpoint
* User with required role accessing an endpoint
* Authenticated user without required role accessing an endpoint

The expected distinction is:

```text
401 Unauthorized
        │
        └── Authentication is missing or invalid

403 Forbidden
        │
        └── Authentication exists but permission is insufficient
```

### Logout Tests

Logout should be tested for:

* Successful logout
* Revocation of refresh token
* Reuse of revoked refresh token
* Missing access token
* Invalid access token
* Expired access token

### Validation Tests

Test cases should include:

* Missing email
* Invalid email format
* Missing password
* Empty fields
* Invalid request body
* Multiple validation failures in one request

The objective is to verify that invalid input does not reach the business logic unexpectedly.

---

## Integration Guide

This module is designed to be integrated into other Spring Boot applications.

### Step 1 — Copy the Security Components

The required security components can be integrated into another Spring Boot project.

The main components include:

```text
SecurityConfig
JwtService
JwtAuthenticationFilter
UserDetailsService
PasswordEncoder
AuthController
AuthService
RefreshTokenService
```

### Step 2 — Configure Database Entities

The authentication system requires persistent user and refresh-token data.

Main entities:

```text
User
RefreshToken
```

The `RefreshToken` entity maintains the relationship with the corresponding user and stores the hashed token, expiration time, revocation status, and creation time.

### Step 3 — Configure JWT Secret

A secure JWT secret should be provided through application configuration or environment variables.

The secret should not be hardcoded into source code or committed to the repository.

Example:

```properties
jwt.secret=${JWT_SECRET}
```

The actual secret should be supplied through the application's environment.

### Step 4 — Configure Token Expiration

The application uses configurable token expiration values.

Current configuration:

```text
Access Token  → 15 minutes
Refresh Token → 30 days
```

These values can be adjusted according to the requirements of the application.

### Step 5 — Configure Spring Security

The security configuration should define:

* Public authentication endpoints
* Protected endpoints
* Authentication provider
* Password encoder
* JWT authentication filter
* Authentication entry point
* Access-denied handler
* Session management policy

The application uses stateless session management for JWT-based authentication.

### Step 6 — Configure Password Encoding

A `PasswordEncoder` should be registered as a Spring bean.

BCrypt is used for password hashing.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### Step 7 — Add JWT Filter

The JWT authentication filter should be registered in the Spring Security filter chain before the appropriate authentication filter.

The filter is responsible for extracting and validating access tokens before protected controllers are executed.

### Step 8 — Configure Exception Handling

Authentication and authorization failures should be handled centrally.

The security configuration distinguishes between:

```text
401 → Authentication failure
403 → Authorization failure
```

The application's API response structure can then be reused for security error responses.

### Step 9 — Configure Refresh Token Storage

Refresh tokens should be stored securely.

The database should store the hash rather than the raw refresh token.

The refresh-token record should also maintain:

```text
User
Expiration
Revoked
Created At
Token Hash
```

### Step 10 — Test Before Integration

Before integrating the module into a larger application, verify:

```text
Register
   ↓
Login
   ↓
Access Protected API
   ↓
Access Token Expires
   ↓
Refresh Token
   ↓
New Access Token
   ↓
Logout
   ↓
Refresh Token Revoked
```

The integration should also verify the negative security scenarios described in the Testing section.

---

## Future Improvements

The module is intended to evolve as additional security concepts are implemented.

Potential improvements include:

* OAuth2 login
* Third-party identity providers
* Refresh-token rotation
* Token reuse detection
* Account lockout
* Rate limiting
* Email verification
* Password reset flow
* Multi-factor authentication
* Security event logging
* More comprehensive automated security tests

OAuth2 integration is currently being explored as the next major authentication feature.

---

## Conclusion

This Spring Security module demonstrates a practical implementation of authentication and authorization using Spring Boot.

The implementation combines:

```text
Spring Security
      +
JWT Access Tokens
      +
Refresh Tokens
      +
BCrypt Password Hashing
      +
Role-Based Authorization
      +
Validation
      +
Exception Handling
      +
Logout / Token Revocation
```

The focus of the module is not only implementing the authentication flow, but also understanding the security decisions, edge cases, limitations, and trade-offs involved in building a production-oriented authentication system.

The module can therefore be used both as a learning reference and as a foundation for integrating authentication and authorization into other Spring Boot applications.
