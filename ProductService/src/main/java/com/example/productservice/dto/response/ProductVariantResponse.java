package com.example.productservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponse {

    Long id;

    String skuCode;

    BigDecimal price;

    BigDecimal salePrice;

    Integer stock;

    Map<String, String> attributes;
}
