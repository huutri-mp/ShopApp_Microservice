package com.example.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description;
}
