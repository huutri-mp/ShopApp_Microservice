package com.example.productservice.service.impl;

import com.example.event.dto.ProductCreatedEvent;
import com.example.productservice.dto.request.ProductCreationRequest;
import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.entity.Category;
import com.example.productservice.entity.Product;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.productservice.repository.CategoryRepository;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public List<ProductResponse> getProduct() {
        List<ProductResponse> productResponses = new ArrayList<>();
        try {
            productRepository.findAll().forEach(product -> {
                ProductResponse response = ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .brand(product.getBrand())
                        .price(product.getPrice())
                        .category(product.getCategory() != null ? product.getCategory().getName() : null)
                        .status(product.getStatus())
                        .build();
                productResponses.add(response);
            });
            return productResponses;
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage());

        }
        return new ArrayList<>();
    }
    public ProductResponse getProductById(Integer productId) {
        return productRepository.findById(productId)
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .brand(product.getBrand())
                        .price(product.getPrice())
                        .category(product.getCategory() != null ? product.getCategory().getName() : null)
                        .status(product.getStatus())
                        .build())
                .orElse(null);
    }

    public List<ProductResponse> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        try {
            return productRepository.findByPriceBetween(minPrice, maxPrice)
                    .stream()
                    .map(product -> ProductResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .brand(product.getBrand())
                            .price(product.getPrice())
                            .category(product.getCategory() != null ? product.getCategory().getName() : null)
                            .status(product.getStatus())
                            .price(product.getPrice())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching products by price range between {} and {}: {}",
                    minPrice, maxPrice, e.getMessage());
            return Collections.emptyList();
        }
    }

    public String createProduct(ProductCreationRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (productRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PRODUCT_ALREADY_EXISTS);
        }

        try {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            // Lưu product trước để lấy được ID
            Product savedProduct = productRepository.save(
                    Product.builder()
                            .name(request.getName())
                            .description(request.getDescription())
                            .price(request.getPrice())
                            .brand(request.getBrand())
                            .category(category)
                            .status(request.getStatus())
                            .quantity(request.getQuantity())
                            .build()
            );

          // Gửi sự kiện Kafka sau khi lưu thành công
            ProductCreatedEvent event = ProductCreatedEvent.builder()
                    .id(savedProduct.getId())
                    .name(savedProduct.getName())
                    .description(savedProduct.getDescription())
                    .price(savedProduct.getPrice())
                    .brand(savedProduct.getBrand())
                    .category(savedProduct.getCategory().getName())
                    .status(savedProduct.getStatus())
                    .build();

            try {
                kafkaTemplate.send("product-created-topic", event);
            } catch (Exception e) {
                log.error("Failed to send Kafka product created envet", e);
            }

            return "Product created successfully";
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            return "Failed to create product";
        }
    }


    public String updateProduct(Integer productId, ProductUpdateRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        log.info("Updating product with id {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (request.getName() != null) {
            if (productRepository.existsByName(request.getName()) &&
                !product.getName().equals(request.getName())) {
                throw new AppException(ErrorCode.PRODUCT_ALREADY_EXISTS);
            }
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getQuantity() != null) {

            int temp = product.getQuantity() + request.getQuantity();
            product.setQuantity(temp);
        }
        try {
            productRepository.save(product);
            return "Product updated successfully";
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            return "Failed to update product";
        }

    }

    public String deleteProduct(Integer productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            productRepository.delete(product);
            return "Product deleted successfully";
        } catch (Exception e) {
            log.error("Error deleting product with id {}: {}", productId, e.getMessage());
            return "Failed to delete product";
        }
    }

    public String updateProductStock(Integer productId, Integer quantity) {
        if (quantity == null) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }
        log.info("Updating stock for product with id {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getQuantity() < quantity || product.getQuantity() == 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }
        int newQuantity = product.getQuantity() - quantity;
        product.setQuantity(newQuantity);
        try {
            productRepository.save(product);
            return "Product stock updated successfully";
        } catch (Exception e) {
            log.error("Error updating product stock: {}", e.getMessage());
            return "Failed to update product stock";
        }
    }
    public Boolean checkProduct (Integer productId, Integer quantity) {
        log.info("Checking if product exists with id {}", productId);
        if (productId == null) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getQuantity() < quantity || product.getQuantity() == 0) {
            log.error("Insufficient stock for product with id {}", productId);
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }
        return true;
    }
}
