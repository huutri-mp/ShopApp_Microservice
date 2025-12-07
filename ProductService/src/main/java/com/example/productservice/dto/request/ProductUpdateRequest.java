package com.example.productservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    String name;
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description;
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    Double price;
    @DecimalMin(value = "0.01", message = "Original price must be greater than 0")
    Double originalPrice;
    Integer categoryId;
    @Min(value = 0, message = "Quantity cannot be negative")
    Integer quantity;
    @Size(max = 100, message = "Brand name cannot exceed 100 characters")
    String brand;
    String status;
}