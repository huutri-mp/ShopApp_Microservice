package com.example.productservice.mapper;

import com.example.productservice.dto.request.ProductVariantRequest;
import com.example.productservice.dto.response.ProductVariantResponse;
import com.example.productservice.entity.ProductVariant;
import org.mapstruct.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "skuCode", ignore = true)
    ProductVariant toEntity(ProductVariantRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "skuCode", ignore = true)
    void updateEntity(
            ProductVariantRequest request,
            @MappingTarget ProductVariant entity
    );

    ProductVariantResponse toResponse(ProductVariant entity);

    List<ProductVariantResponse> toResponses(List<ProductVariant> entities);
}
