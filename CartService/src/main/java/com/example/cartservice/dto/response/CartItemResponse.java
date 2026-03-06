package com.example.cartservice.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long id;
    String skuCode;
    Long productId;
    String name;
    String imageUrl;
    Integer quantity;
    BigDecimal price;
}
