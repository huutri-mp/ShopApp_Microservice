package com.example.productservice.mapper;

import com.example.commonlib.dto.ProductEvent;
import com.example.commonlib.dto.EventType;
import com.example.productservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
@Mapper(componentModel = "spring")
public interface ProductEventMapper {

    @Mapping(target = "eventType", source = "eventType")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "categoryId", source = "product.category.id")
    @Mapping(target = "brandId", source = "product.brand.id")
    @Mapping(
            target = "image",
            expression = "java(com.example.productservice.mapper.ProductEventMapper.mapFirstImage(product))"
    )
    @Mapping(
            target = "minPrice",
            expression = "java(com.example.productservice.mapper.ProductEventMapper.calcMinPrice(product))"
    )
    @Mapping(
            target = "maxPrice",
            expression = "java(com.example.productservice.mapper.ProductEventMapper.calcMaxPrice(product))"
    )
    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "brandName", source = "product.brand.name")
    @Mapping(target = "isFeatured", source = "product.isFeatured")
    ProductEvent toEvent(EventType eventType, Product product);

    static String mapFirstImage(Product product) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return null;
        }
        return product.getImages().get(0).getUrl();
    }

    static BigDecimal calcMinPrice(Product product) {
        if (product.getVariants() == null || product.getVariants().isEmpty()) return null;
        return product.getVariants().stream()
                .map(v -> v.getPrice())
                .min(BigDecimal::compareTo)
                .orElse(null);
    }

    static BigDecimal calcMaxPrice(Product product) {
        if (product.getVariants() == null || product.getVariants().isEmpty()) return null;
        return product.getVariants().stream()
                .map(v -> v.getPrice())
                .max(BigDecimal::compareTo)
                .orElse(null);
    }
}
