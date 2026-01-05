//package com.example.productservice.controller;
//
//import com.example.commonlib.dto.ApiResponse;
//import com.example.commonlib.dto.PagingResponse;
//import com.example.productservice.dto.response.ProductImageResponse;
//import com.example.productservice.service.ProductImageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/images")
//@RequiredArgsConstructor
//public class ProductImageController {
//
//    private final ProductImageService imageService;
//
//    @PostMapping
//    public ApiResponse<ProductImageResponse> create(@RequestBody ProductImageRequest request,
//                                                    @RequestParam(required = false) Long productId) {
//        ProductImageResponse result = imageService.create(request, productId);
//        return ApiResponse.<ProductImageResponse>builder()
//                .code(200)
//                .message("Create image successfully")
//                .data(result)
//                .build();
//    }
//
//    @GetMapping("/{id}")
//    public ApiResponse<ProductImageResponse> getById(@PathVariable Long id) {
//        ProductImageResponse result = imageService.getById(id);
//        return ApiResponse.<ProductImageResponse>builder().data(result).build();
//    }
//
//    @GetMapping
//    public ApiResponse<PagingResponse<ProductImageResponse>> getAll(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        PagingResponse<ProductImageResponse> paging = imageService.getAll(page, size);
//        return ApiResponse.<PagingResponse<ProductImageResponse>>builder().data(paging).build();
//    }
//
//    @PutMapping("/{id}")
//    public ApiResponse<ProductImageResponse> update(@PathVariable Long id,
//                                                    @RequestBody ProductImageRequest request,
//                                                    @RequestParam(required = false) Long productId) {
//        ProductImageResponse updated = imageService.update(id, request, productId);
//        return ApiResponse.<ProductImageResponse>builder()
//                .message("Update image successfully")
//                .data(updated)
//                .build();
//    }
//
//    @DeleteMapping("/{id}")
//    public ApiResponse<Void> delete(@PathVariable Long id) {
//        imageService.delete(id);
//        return ApiResponse.<Void>builder().message("Delete image successfully").build();
//    }
//}
