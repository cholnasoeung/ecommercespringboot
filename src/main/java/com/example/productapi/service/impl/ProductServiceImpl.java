package com.example.productapi.service.impl;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.repository.ProductRepository;
import com.example.productapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
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