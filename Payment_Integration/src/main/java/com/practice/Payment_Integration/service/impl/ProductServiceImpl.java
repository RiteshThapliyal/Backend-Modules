package com.practice.Payment_Integration.service.impl;

import com.practice.Payment_Integration.dto.request.ProductRequest;
import com.practice.Payment_Integration.dto.response.ProductResponse;
import com.practice.Payment_Integration.entity.Product;
import com.practice.Payment_Integration.exception.ProductNotFoundException;
import com.practice.Payment_Integration.repository.ProductRepository;
import com.practice.Payment_Integration.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct (ProductRequest request)
    {
        Product product = Product.builder()
                .name(request.getName().trim())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById (Long id)
    {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getProducts ()
    {
        return productRepository.findAll()
                .stream()
                .filter(product -> Boolean.TRUE.equals(product.getActive()))
                .filter(product -> product.getStock() > 0)
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse mapToResponse (Product product)
    {
        return ProductResponse.builder()

                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .active(product.getActive())
                .build();
    }
}
