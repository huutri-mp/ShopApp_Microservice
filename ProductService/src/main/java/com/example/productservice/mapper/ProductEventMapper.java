package com.example.productservice.mapper;

import com.example.event.dto.EventType;
import com.example.event.dto.ProductEvent;
import com.example.productservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
@Mapper(componentModel = "spring")
public interface ProductEventMapper {

    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "categoryId", source = "product.category.id")
    @Mapping(target = "brandId", source = "product.brand.id")

    @Mapping(target = "images", expression = "java(com.example.productservice.mapper.ProductEventMapper.mapImages(product))")
    @Mapping(target = "variants", expression = "java(com.example.productservice.mapper.ProductEventMapper.mapVariants(product))")
    @Mapping(target = "minPrice", expression = "java(com.example.productservice.mapper.ProductEventMapper.calcMinPrice(product))")
    @Mapping(target = "maxPrice", expression = "java(com.example.productservice.mapper.ProductEventMapper.calcMaxPrice(product))")

    ProductEvent toEvent(EventType eventType, Product product);

    static List<String> mapImages(Product product) {
        if (product.getImages() == null) return List.of();
        return product.getImages().stream()
                .map(img -> img.getUrl())
                .toList();
    }

    static List<Map<String, Object>> mapVariants(Product product) {
        if (product.getVariants() == null) return List.of();
        return product.getVariants().stream()
                .map(v -> Map.<String, Object>of(
                        "id", v.getId(),
                        "sku", v.getSku(),
                        "color", v.getColor(),
                        "size", v.getSize(),
                        "price", v.getPrice(),
                        "salePrice", v.getSalePrice(),
                        "stock", v.getStock()
                ))
                .toList();
    }

    static Double calcMinPrice(Product product) {
        if (product.getVariants() == null || product.getVariants().isEmpty()) return null;
        return product.getVariants().stream()
                .map(v -> v.getPrice())
                .min(Double::compareTo)
                .orElse(null);
    }

    static Double calcMaxPrice(Product product) {
        if (product.getVariants() == null || product.getVariants().isEmpty()) return null;
        return product.getVariants().stream()
                .map(v -> v.getPrice())
                .max(Double::compareTo)
                .orElse(null);
    }
}
