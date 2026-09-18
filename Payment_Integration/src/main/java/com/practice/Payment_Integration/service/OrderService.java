package com.practice.Payment_Integration.service;

import com.practice.Payment_Integration.dto.request.OrderRequest;
import com.practice.Payment_Integration.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder (OrderRequest request);
}
