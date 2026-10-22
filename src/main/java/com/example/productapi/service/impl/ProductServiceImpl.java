package com.example.productapi.service.impl;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import com.example.productapi.exception.ResourceNotFoundException;
import com.example.productapi.mapper.ProductMapper;
import com.example.productapi.repository.ProductRepository;
import com.example.productapi.service.FileService;
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
    private final FileService fileService; // <-- Added FileService

    @Override
    public ProductResponse create(ProductRequest request) {
        Product saved = repository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Product> productPage;
        if (search != null && !search.isEmpty()) {
            productPage = repository.findByNameContainingIgnoreCase(search, pageable);
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

        // Save the file and get the URL
        String imageUrl = fileService.saveImage(file);

        // Save the URL to the database
        product.setImageUrl(imageUrl);
        repository.save(product);

        return imageUrl;
    }

    private Product getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));
    }
}