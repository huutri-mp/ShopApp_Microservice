package com.example.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEvent {
    private EventType eventType;
    private Long productId;
    private String slug;
    private String name;
    private String description;
    private Boolean active;
    private Long categoryId;
    private Long brandId;
    private List<String> images;
    private Double minPrice;
    private Double maxPrice;
    private List<Map<String, Object>> variants;
}