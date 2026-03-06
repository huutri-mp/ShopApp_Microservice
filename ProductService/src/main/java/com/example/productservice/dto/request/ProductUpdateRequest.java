package com.example.productservice.dto.request;

import lombok.*;

import java.util.List;

@Data
public class ProductUpdateRequest {

    private String name;

    private String description;

    private Long categoryId;

    private Long brandId;

    private List<Long> removedImageIds;

    private Boolean isFeatured;

    private List<ProductVariantRequest> variants;
}
