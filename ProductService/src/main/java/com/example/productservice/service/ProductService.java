package com.example.productservice.service;

import com.example.commonlib.dto.OrderEvent;
import com.example.productservice.dto.request.ProductCreationRequest;
import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.entity.ProductVariant;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductCreationRequest req, List<MultipartFile> files);
    ProductResponse update(Long id, ProductUpdateRequest req, List<MultipartFile> files);
    void delete(Long id);
    PagingResponse<ProductResponse> getProducts(Integer page, Integer size, String keyword, Boolean isDesc, Long categoryId, Long brandId);
    ProductResponse getProductById(Long id);
    List<ProductVariant> getProductsForOrder(List<String>skuCodes);
    void updateStockProduct (OrderEvent event);
}
