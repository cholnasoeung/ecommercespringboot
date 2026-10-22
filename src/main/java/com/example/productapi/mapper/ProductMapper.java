package com.example.productapi.mapper;

import com.example.productapi.dto.ProductRequest;
import com.example.productapi.dto.ProductResponse;
import com.example.productapi.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest r) {
        return Product.builder()
                .name(r.name())
                .description(r.description())
                .price(r.price())
                .quantity(r.quantity())
                .build();
    }

    public void updateEntity(Product p, ProductRequest r) {
        p.setName(r.name());
        p.setDescription(r.description());
        p.setPrice(r.price());
        p.setQuantity(r.quantity());
    }

    public ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getQuantity(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}