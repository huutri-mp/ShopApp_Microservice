package com.example.productservice.service.impl;

import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.mapper.CategoryMapper;
import com.example.productservice.dto.request.CategoryRequest;
import com.example.productservice.dto.response.CategoryResponse;
import com.example.productservice.entity.Category;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_NAME);
        }

        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        Category category = Category.builder()
                .name(request.getName())
                .parent(parent)
                .build();

        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_NAME);
        }

        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            category.setParent(parent);
        }

        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (categoryRepository.existsByParent_Id(id)) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_NAME);
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public PagingResponse<CategoryResponse> getCategories(
            Integer page,
            Integer size,
            String keyword,
            Boolean isDesc
    ) {
        Sort sort = Boolean.TRUE.equals(isDesc)
                ? Sort.by("name").descending()
                : Sort.by("name").ascending();

        Specification<Category> spec = (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%"
            );
        };

        if (page == null || size == null) {
            List<Category> categories = categoryRepository.findAll(spec, sort);

            return PagingResponse.<CategoryResponse>builder()
                    .items(categories.stream()
                            .map(categoryMapper::toCategoryResponse)
                            .toList())
                    .total(categories.size())
                    .build();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categoryPage =
                categoryRepository.findAll(spec, pageable);

        return PagingResponse.<CategoryResponse>builder()
                .items(categoryPage.getContent()
                        .stream()
                        .map(categoryMapper::toCategoryResponse)
                        .toList())
                .total(categoryPage.getTotalElements())
                .page(page)
                .size(size)
                .hasNext(categoryPage.hasNext())
                .hasPrev(categoryPage.hasPrevious())
                .build();
    }


}
