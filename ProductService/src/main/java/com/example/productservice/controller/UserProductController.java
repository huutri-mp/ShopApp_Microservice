package com.example.productservice.controller;

import com.example.productservice.constan.UrlConstant;
import com.example.productservice.dto.request.ProductCreationRequest;
import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_PRODUCT_USER)
public class UserProductController {

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("Fetching all products for user");
        List<ProductResponse> productResponses = productService.getProduct();

        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer productId) {
        log.info("Fetching product with ID: {}", productId);
        ProductResponse productResponse = productService.getProductById(productId);
        return ResponseEntity.ok(productResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(@RequestBody ProductCreationRequest request) {
        log.info("Creating new product: {}", request);
        String response = productService.createProduct(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Integer productId, @RequestBody ProductUpdateRequest request) {
        log.info("Updating product with ID: {}", productId);
        String response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer productId) {
        log.info("Deleting product with ID: {}", productId);
        String response = productService.deleteProduct(productId);
        return ResponseEntity.ok(response);
    }


}
