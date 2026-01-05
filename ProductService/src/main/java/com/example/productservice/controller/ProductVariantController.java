package com.example.productservice.controller;

import com.example.commonlib.dto.ApiResponse;
import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.dto.request.ProductVariantRequest;
import com.example.productservice.dto.response.ProductVariantResponse;
import com.example.productservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;

    @PostMapping
    public ApiResponse<ProductVariantResponse> create(@RequestBody ProductVariantRequest request,
                                                      @RequestParam(required = false) Long productId) {
        ProductVariantResponse result = variantService.create(request, productId);
        return ApiResponse.<ProductVariantResponse>builder()
                .code(200)
                .message("Create variant successfully")
                .data(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductVariantResponse> getById(@PathVariable Long id) {
        ProductVariantResponse result = variantService.getById(id);
        return ApiResponse.<ProductVariantResponse>builder().data(result).build();
    }

    @GetMapping
    public ApiResponse<PagingResponse<ProductVariantResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagingResponse<ProductVariantResponse> paging = variantService.getAll(page, size);
        return ApiResponse.<PagingResponse<ProductVariantResponse>>builder().data(paging).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductVariantResponse> update(@PathVariable Long id,
                                                      @RequestBody ProductVariantRequest request,
                                                      @RequestParam(required = false) Long productId) {
        ProductVariantResponse updated = variantService.update(id, request, productId);
        return ApiResponse.<ProductVariantResponse>builder()
                .message("Update variant successfully")
                .data(updated)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        variantService.delete(id);
        return ApiResponse.<Void>builder().message("Delete variant successfully").build();
    }
}
