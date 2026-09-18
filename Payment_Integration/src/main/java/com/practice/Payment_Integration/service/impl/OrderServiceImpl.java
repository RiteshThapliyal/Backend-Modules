package com.practice.Payment_Integration.service.impl;

import com.practice.Payment_Integration.dto.request.OrderRequest;
import com.practice.Payment_Integration.dto.response.OrderResponse;
import com.practice.Payment_Integration.entity.Order;
import com.practice.Payment_Integration.entity.Product;
import com.practice.Payment_Integration.entity.User;
import com.practice.Payment_Integration.enums.OrderStatus;
import com.practice.Payment_Integration.exception.InsufficientStockException;
import com.practice.Payment_Integration.exception.ProductNotFoundException;
import com.practice.Payment_Integration.exception.ProductUnavailableException;
import com.practice.Payment_Integration.exception.UserNotFoundException;
import com.practice.Payment_Integration.repository.OrderRepository;
import com.practice.Payment_Integration.repository.ProductRepository;
import com.practice.Payment_Integration.repository.UserRepository;
import com.practice.Payment_Integration.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + request.getUserId()
                        ));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + request.getProductId()
                        ));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ProductUnavailableException(
                    "Product is currently unavailable"
            );
        }

        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName()
            );
        }

        BigDecimal totalAmount = product.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .userId(user.getId())
                .productId(product.getId())
                .productName(product.getName())
                .unitPrice(product.getPrice())
                .quantity(request.getQuantity())
                .totalAmount(totalAmount)
                .status(OrderStatus.PAYMENT_PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(Order order) {

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .unitPrice(order.getUnitPrice())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
