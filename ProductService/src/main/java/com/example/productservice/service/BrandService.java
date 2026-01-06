package com.example.productservice.service;

import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.dto.request.BrandRequest;
import com.example.productservice.dto.response.BrandResponse;

public interface BrandService {
    BrandResponse create(BrandRequest request);
    BrandResponse getById(Long id);
    PagingResponse<BrandResponse> getBrands(Integer page, Integer size, String keyword, Boolean isDesc);
    BrandResponse update(Long id, BrandRequest request);
    void delete(Long id);
}
