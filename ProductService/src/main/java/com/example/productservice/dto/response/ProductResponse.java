package com.example.productservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String slug;
    String description;
    Boolean active;
    CategoryResponse category;
    BrandResponse brand;
    Boolean isFeatured;
    List<ProductImageResponse> images;
    List<ProductVariantResponse> variants;
}
