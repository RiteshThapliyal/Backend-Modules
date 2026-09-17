package com.practice.Payment_Integration.controller;

import com.practice.Payment_Integration.dto.request.ProductRequest;
import com.practice.Payment_Integration.dto.response.ApiResponse;
import com.practice.Payment_Integration.dto.response.ProductResponse;
import com.practice.Payment_Integration.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct (@Valid @RequestBody ProductRequest request)
    {
        ProductResponse productResponse = productService.createProduct(request);

        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .status("SUCCESS")
                .message("Product created successfully")
                .data(productResponse)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById (@PathVariable Long id)
    {
        ProductResponse productResponse = productService.getProductById(id);

        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .status("SUCCESS")
                .message("Product retrieved successfully")
                .data(productResponse)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts ()
    {
        List<ProductResponse> productResponses = productService.getProducts();

        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                        .status("SUCCESS")
                        .message("Products retrieved successfully")
                        .data(productResponses)
                        .timestamp(Instant.now())
                        .build();

        return ResponseEntity.ok(response);
    }
}
