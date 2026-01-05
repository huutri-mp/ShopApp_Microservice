package com.example.productservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponse {

    Long id;

    String sku;

    String color;

    String size;

    Double price;

    Double salePrice;

    Integer stock;

    // Tech retail attributes
    String storage;
    String ram;
    String cpu;
    String gpu;
    String screenSize;
    String screenResolution;
    String batteryCapacity;
    String connectivity;
    Integer warrantyMonths;
    String weight;
    String dimensions;
    String material;
    String itemCondition;
    Integer releaseYear;
}
