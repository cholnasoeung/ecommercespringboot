package com.example.productapi.repository;

import com.example.productapi.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Used for search functionality
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // (Optional) kept from previous steps if you used it
    boolean existsByName(String name);
}