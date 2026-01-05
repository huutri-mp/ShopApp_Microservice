package com.example.productservice.controller;

import com.example.commonlib.dto.ApiResponse;
import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.constant.UrlConstant;
import com.example.productservice.dto.request.CategoryRequest;

import com.example.productservice.dto.response.CategoryResponse;
import com.example.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UrlConstant.API_V1_CATEGORY)
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse> create(@RequestBody CategoryRequest request) {
        CategoryResponse result = categoryService.createCategory(request);
        return ApiResponse.<CategoryResponse>builder()
                .code(200)
                .message("Create category successfully")
                .data(result)
                .build();
    }


    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable Long id) {
        CategoryResponse result = categoryService.getCategoryById(id);
        return ApiResponse.<CategoryResponse>builder()
                .data(result)
                .build();
    }


    @GetMapping
    public PagingResponse<CategoryResponse> getCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isDesc
    ) {
        PagingResponse<CategoryResponse> response = categoryService.getCategories(page, size, keyword, isDesc);

        return response;
    }


    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(
            @PathVariable Long id,
            @RequestBody CategoryRequest request
    ) {
        CategoryResponse updated = categoryService.updateCategory(id, request);

        return ApiResponse.<CategoryResponse>builder()
                .message("Update category successfully")
                .data(updated)
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);

        return ApiResponse.<Void>builder()
                .message("Delete category successfully")
                .build();
    }
}
