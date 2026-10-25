package com.example.productapi.service.impl;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.repository.ProductRepository;
import com.example.productapi.service.StorageService;
import com.example.productapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final StorageService storageService;

    @Override
    public ProductResponse create(ProductRequest request) {
        Product saved = repository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(String search, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Product> productPage;

        boolean hasSearch = search != null && !search.isEmpty();
        boolean hasCategory = category != null && !category.isEmpty();

        if (hasSearch && hasCategory) {
            productPage = repository.findByNameContainingIgnoreCaseAndCategory_NameIgnoreCase(search, category, pageable);
        } else if (hasSearch) {
            productPage = repository.findByNameContainingIgnoreCase(search, pageable);
        } else if (hasCategory) {
            productPage = repository.findByCategory_NameIgnoreCase(category, pageable);
        } else {
            productPage = repository.findAll(pageable);
        }

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

    @Override
    public String uploadProductImage(Long productId, MultipartFile file) throws IOException {
        Product product = getOrThrow(productId);
        String imagePath = storageService.uploadFile(file);
        product.setImageUrl(imagePath);
        repository.save(product);
        return imagePath;
    }

    private Product getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));
    }
}