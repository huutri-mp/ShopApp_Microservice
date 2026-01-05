package com.example.productservice.controller;

import com.example.commonlib.dto.ApiResponse;
import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.constant.UrlConstant;
import com.example.productservice.dto.request.ProductCreationRequest;
import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_PRODUCT)
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    public PagingResponse<ProductResponse> getProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isDesc,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId
    ) {
        log.info(
                " controller getProducts size={}, page={}, keyword={}, isDesc={}, categoryId={}, brandId={}",
                size,
                page,
                keyword,
                isDesc,
                categoryId,
                brandId
        );

        PagingResponse<ProductResponse> response = productService.getProducts(page, size, keyword, isDesc, categoryId, brandId);
        return response;

    }
//
//    @GetMapping("/{productId}")
//    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer productId) {
//        log.info("Fetching product with ID: {}", productId);
//        ProductResponse productResponse = productService.getProductById(productId);
//        return ResponseEntity.ok(productResponse);
//    }


    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<ProductResponse> create(
            @RequestPart("product") ProductCreationRequest request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> files) {
        log.info("Creating product: {}", request);
        log.info("Creating product image: {}", files);
        ProductResponse response = productService.create(request, files);
        ApiResponse<ProductResponse> result = ApiResponse.<ProductResponse>builder()
                .data(response)
                .build();
        return result;
    }

    @PutMapping("/{productId}")
    public ApiResponse<Void> updateProduct(
            @PathVariable Long productId,
            @RequestPart(value = "productUpdate", required = false) ProductUpdateRequest request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> files
            ) {
        productService.update(productId, request, files);
        return ApiResponse.<Void>builder()
                .message("Update product successfully")
                .build();
    }


    @DeleteMapping("/{productId}")
    public ApiResponse<Void> delete(@PathVariable Long productId) {
        productService.delete(productId);
        return ApiResponse.<Void>builder()
                .message("Delete product successfully")
                .build();
    }

}
