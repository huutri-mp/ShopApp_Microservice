package com.example.productservice.controller;

import com.example.commonlib.dto.ApiResponse;
import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.constant.UrlConstant;
import com.example.productservice.dto.request.BrandRequest;
import com.example.productservice.dto.response.BrandResponse;
import com.example.productservice.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UrlConstant.API_V1_BRAND)
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ApiResponse<BrandResponse> create(@RequestBody BrandRequest request) {
        BrandResponse result = brandService.create(request);
        return ApiResponse.<BrandResponse>builder()
                .code(200)
                .message("Create brand successfully")
                .data(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getById(@PathVariable Long id) {
        BrandResponse result = brandService.getById(id);
        return ApiResponse.<BrandResponse>builder().data(result).build();
    }

    @GetMapping
    public PagingResponse<BrandResponse> getBrands(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isDesc
    ) {
        PagingResponse<BrandResponse> paging = brandService.getBrands(page, size, keyword, isDesc);
        return paging;
    }

    @PutMapping("/{id}")
    public ApiResponse<BrandResponse> update(@PathVariable Long id, @RequestBody BrandRequest request) {
        BrandResponse updated = brandService.update(id, request);
        return ApiResponse.<BrandResponse>builder()
                .message("Update brand successfully")
                .data(updated)
                .build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ApiResponse.<Void>builder().message("Delete brand successfully").build();
    }
}
