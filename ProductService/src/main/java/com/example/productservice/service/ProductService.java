package com.example.productservice.service;

import com.example.productservice.dto.request.ProductCreationRequest;
import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.commonlib.dto.PagingResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductCreationRequest req, List<MultipartFile> files);
    ProductResponse update(Long id, ProductUpdateRequest req, List<MultipartFile> files);
    void delete(Long id);
    ProductResponse getById(Long id);
    ProductResponse getBySlug(String slug);
    PagingResponse<ProductResponse> getProducts(Integer page, Integer size, String keyword, Boolean isDesc, Long categoryId, Long brandId);

}
