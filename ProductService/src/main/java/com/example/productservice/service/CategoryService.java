package com.example.productservice.service;

import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.dto.request.CategoryRequest;
import com.example.productservice.dto.response.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

    CategoryResponse getCategoryById(Long id);

    PagingResponse<CategoryResponse> getCategories(Integer page, Integer size, String keyword, Boolean isDesc);
}

