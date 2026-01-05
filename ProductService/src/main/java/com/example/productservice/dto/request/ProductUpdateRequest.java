package com.example.productservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Array;
import java.util.List;

@Data
public class ProductUpdateRequest {

    private String name;

    private String description;

    private Long categoryId;

    private Long brandId;

    private List<Long> removedImageIds;

    private List<ProductVariantRequest> variants;
}
