package com.example.productapi.repository;

import com.example.productapi.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Add these two methods for filtering by category
    Page<Product> findByCategory_NameIgnoreCase(String categoryName, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategory_NameIgnoreCase(String name, String categoryName, Pageable pageable);

    boolean existsByName(String name);
}