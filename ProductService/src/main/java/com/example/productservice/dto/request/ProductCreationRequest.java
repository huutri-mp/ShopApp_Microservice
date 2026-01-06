package com.example.productservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
public class ProductCreationRequest {

    @NotBlank
    private String name;

    private String description;

    private Boolean active;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long brandId;

    private List<ProductVariantRequest> variants;
}
