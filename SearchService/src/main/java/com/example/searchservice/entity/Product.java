package com.example.searchservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;


@Document(indexName = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private Long id;
    private String name;
    private Boolean isFeatured;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private String image;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
