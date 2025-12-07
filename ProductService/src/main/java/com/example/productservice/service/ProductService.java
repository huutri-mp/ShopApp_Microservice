package com.example.productservice.service;

import com.example.productservice.dto.request.ProductCreationRequest;
import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<ProductResponse> getProduct();
    ProductResponse getProductById(Integer productId);
    List<ProductResponse> getProductsByPriceRange (Double minPrice, Double maxPrice);
    String createProduct(ProductCreationRequest request);
    String updateProduct (Integer productId, ProductUpdateRequest request);
    String deleteProduct(Integer productId);
    String updateProductStock (Integer productId, Integer quantity);
    Boolean checkProduct(Integer productId, Integer quantity);
}
