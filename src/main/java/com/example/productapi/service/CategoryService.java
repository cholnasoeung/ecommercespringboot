package com.example.productapi.service;

import com.example.productapi.dto.CategoryRequest;
import com.example.productapi.entity.Category;
import com.example.productapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryRequest request) {
        // Check if category already exists to prevent database crash
        boolean exists = categoryRepository.findAll().stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(request.name()));

        if (exists) {
            throw new RuntimeException("Category '" + request.name() + "' already exists!");
        }

        Category category = new Category();
        category.setName(request.name());
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}