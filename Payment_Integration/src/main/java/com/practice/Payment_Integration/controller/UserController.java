package com.practice.Payment_Integration.controller;

import com.practice.Payment_Integration.dto.request.UserRequest;
import com.practice.Payment_Integration.dto.response.ApiResponse;
import com.practice.Payment_Integration.dto.response.UserResponse;
import com.practice.Payment_Integration.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser (@Valid @RequestBody UserRequest request)
    {
        UserResponse userResponse = userService.createUser(request);

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status("SUCCESS")
                .message("User created successfully")
                .data(userResponse)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById (@PathVariable Long id)
    {
        UserResponse userResponse = userService.getUserById(id);

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status("SUCCESS")
                .message("User retrieved successfully")
                .data(userResponse)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers ()
    {
        List<UserResponse> userResponseList = userService.getAllUsers();

        ApiResponse<List<UserResponse>> response = ApiResponse.<List<UserResponse>>builder()
                .status("SUCCESS")
                .message("Users retrieved successfully")
                .data(userResponseList)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
