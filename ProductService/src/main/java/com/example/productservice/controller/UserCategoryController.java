package com.example.productservice.controller;


import com.example.productservice.constan.UrlConstant;
import com.example.productservice.dto.request.CategoryRequest;
import com.example.productservice.dto.response.CategoryResponse;
import com.example.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_CATEGORY_USER)
public class UserCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.info("Fetching all categories for user");
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Integer categoryId) {
        log.info("Fetching category with ID: {}", categoryId);
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createCategory(@RequestBody CategoryRequest request) {
        log.info("Creating new category: {}", request);
        String response = categoryService.createCategory(request);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer categoryId) {
        log.info("Deleting category with ID: {}", categoryId);
        String response = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Integer categoryId, @RequestBody CategoryRequest request) {
        log.info("Updating category with ID: {} with data: {}", categoryId, request);
        String response = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(response);
    }


}

