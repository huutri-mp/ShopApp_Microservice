package com.example.productservice.service;

import com.example.productservice.dto.request.CategoryRequest;
import com.example.productservice.dto.response.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Integer id);
    String createCategory(CategoryRequest request);
    String deleteCategory(Integer id);
    String updateCategory(Integer id, CategoryRequest request);
}
