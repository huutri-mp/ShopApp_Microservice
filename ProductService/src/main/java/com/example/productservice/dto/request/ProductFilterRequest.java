package com.example.productservice.dto.request;

import lombok.Data;

@Data
public class ProductFilterRequest {
    private String keyword;
    private Long categoryId;
    private Long brandId;

    private Double minPrice;
    private Double maxPrice;

    private int page = 0;
    private int size = 20;
}

