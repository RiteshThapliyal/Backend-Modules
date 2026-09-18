package com.practice.Payment_Integration.controller;

import com.practice.Payment_Integration.dto.request.OrderRequest;
import com.practice.Payment_Integration.dto.response.ApiResponse;
import com.practice.Payment_Integration.dto.response.OrderResponse;
import com.practice.Payment_Integration.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder (@Valid @RequestBody OrderRequest request)
    {
        OrderResponse orderResponse = orderService.createOrder(request);

        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .status("SUCCESS")
                .message("Order created successfully")
                .data(orderResponse)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
