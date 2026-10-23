package com.example.productapi.service;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException; // <--- THIS WAS MISSING

public interface ProductService {

    ProductResponse create(ProductRequest request);

    // Change the findAll signature to include category
    Page<ProductResponse> findAll(String search, String category, int page, int size);

    ProductResponse findById(Long id);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    // Image upload method
    String uploadProductImage(Long productId, MultipartFile file) throws IOException;
}