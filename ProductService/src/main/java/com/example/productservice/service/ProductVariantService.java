package com.example.productservice.service;

import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.dto.request.ProductVariantRequest;
import com.example.productservice.dto.response.ProductVariantResponse;

public interface ProductVariantService {
    ProductVariantResponse create(ProductVariantRequest request, Long productId);
    ProductVariantResponse getById(Long id);
    PagingResponse<ProductVariantResponse> getAll(int page, int size);
    ProductVariantResponse update(Long id, ProductVariantRequest request, Long productId);
    void delete(Long id);
}
