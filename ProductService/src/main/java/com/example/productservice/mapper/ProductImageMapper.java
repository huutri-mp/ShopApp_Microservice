package com.example.productservice.mapper;

import com.example.productservice.dto.response.ProductImageResponse;
import com.example.productservice.entity.ProductImage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    default List<String> toResponses(List<ProductImage> images) {
        if (images == null) return List.of();
        return images.stream().map(ProductImage::getUrl).toList();
    }

    default ProductImageResponse toResponse(ProductImage image) {
        if (image == null) return null;
        return ProductImageResponse.builder()
                .id(image.getId())
                .url(image.getUrl())
                .build();
    }
}
