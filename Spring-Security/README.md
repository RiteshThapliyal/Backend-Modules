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

Architecture

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

