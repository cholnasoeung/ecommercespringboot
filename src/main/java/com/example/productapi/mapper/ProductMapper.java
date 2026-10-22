package com.example.productapi.mapper;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Category;
import com.example.productapi.entity.Product;
import com.example.productapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryRepository categoryRepository;

    public Product toEntity(ProductRequest r) {
        Category category = null;
        if (r.categoryId() != null) {
            category = categoryRepository.findById(r.categoryId()).orElse(null);
        }

        return Product.builder()
                .name(r.name())
                .description(r.description())
                .price(r.price())
                .stockQuantity(r.stockQuantity())
                .category(category)
                .build();
    }

    public void updateEntity(Product p, ProductRequest r) {
        Category category = null;
        if (r.categoryId() != null) {
            category = categoryRepository.findById(r.categoryId()).orElse(null);
        }

        p.setName(r.name());
        p.setDescription(r.description());
        p.setPrice(r.price());
        p.setStockQuantity(r.stockQuantity());
        p.setCategory(category);
    }

    public ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getImageUrl(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}