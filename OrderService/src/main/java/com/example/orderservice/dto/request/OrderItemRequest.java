package com.example.orderservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {
    @NotBlank(message = "SkuCode is required")
    String skuCode;

    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity;
}
