package com.example.productservice.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantRequest {
    Long id;
    BigDecimal price;
    BigDecimal salePrice;
    Integer stock;
    Map<String, String> attributes;
}
