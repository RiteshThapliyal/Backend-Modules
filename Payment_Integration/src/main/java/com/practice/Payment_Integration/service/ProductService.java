package com.practice.Payment_Integration.service;

import com.practice.Payment_Integration.dto.request.ProductRequest;
import com.practice.Payment_Integration.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct (ProductRequest request);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getProducts ();
}
