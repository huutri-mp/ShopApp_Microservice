package com.example.productservice.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantRequest {
    Long id;
    String sku;
    String color;
    String size;
    Double price;
    Double salePrice;
    Integer stock;
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
