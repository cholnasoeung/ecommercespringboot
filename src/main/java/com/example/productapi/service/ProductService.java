package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    List<ProductResponse> findAll();
    ProductResponse findById(Long id);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
}