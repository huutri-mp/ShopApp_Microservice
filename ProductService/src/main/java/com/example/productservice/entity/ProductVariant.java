package com.example.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    String material;
    Integer releaseYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;
}
