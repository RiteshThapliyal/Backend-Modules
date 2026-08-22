package com.project.SpringSecurity.controller;

import com.project.SpringSecurity.common.constants.ApiMessages;
import com.project.SpringSecurity.common.response.ApiResponse;
import com.project.SpringSecurity.common.response.ApiResponseBuilder;
import com.project.SpringSecurity.dto.request.LoginRequest;
import com.project.SpringSecurity.dto.request.RefreshTokenRequest;
import com.project.SpringSecurity.dto.request.RegisterRequest;
import com.project.SpringSecurity.dto.response.LoginResponse;
import com.project.SpringSecurity.dto.response.RegisterResponse;
import com.project.SpringSecurity.service.AuthService;
import com.project.SpringSecurity.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register (@Valid @RequestBody RegisterRequest request)
    {
        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success(
                        ApiMessages.USER_REGISTERED,
                        response
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login (@Valid @RequestBody LoginRequest request)
    {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponseBuilder.success("Login Successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh (@Valid @RequestBody RefreshTokenRequest request)
    {
        LoginResponse response = refreshTokenService.verifyRefreshToken(request.getRefreshToken());

        return ResponseEntity.ok(ApiResponseBuilder.success("Access token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @Valid @RequestBody RefreshTokenRequest request)
    {
        refreshTokenService.revokeRefreshToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                ApiResponseBuilder.success(
                        "Logout successful",
                        "User logged out successfully"
                )
        );
    }
}
