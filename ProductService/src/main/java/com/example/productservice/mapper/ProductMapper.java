package com.example.productservice.mapper;

import com.example.productservice.dto.request.ProductUpdateRequest;
import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.entity.Product;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                CategoryMapper.class,
                BrandMapper.class,
                ProductImageMapper.class,
                ProductVariantMapper.class
        }
)
public interface ProductMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductResponse toResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateProduct(
            @MappingTarget Product entity,
            ProductUpdateRequest request
    );
}
