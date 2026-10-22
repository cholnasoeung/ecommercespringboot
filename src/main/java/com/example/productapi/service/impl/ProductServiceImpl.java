package com.example.productapi.service.impl;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.repository.ProductRepository;
import com.example.productapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public ProductResponse create(ProductRequest request) {
        Product saved = repository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(String search, int page, int size) {
        // Create pageable object (page 0, size 10, sorted by id ascending)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Product> productPage;

        // If there's a search query, search by name. Otherwise, get all.
        if (search != null && !search.isEmpty()) {
            productPage = repository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            productPage = repository.findAll(pageable);
        }

        // Convert the Page of Entities to a Page of DTOs
        return productPage.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product p = getOrThrow(id);
        mapper.updateEntity(p, request);
        return mapper.toResponse(repository.save(p));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Product getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));
    }
}