package com.example.productservice.mapper;

import com.example.productservice.dto.request.ProductVariantRequest;
import com.example.productservice.dto.response.ProductVariantResponse;
import com.example.productservice.entity.ProductVariant;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductVariantResponse toResponse(ProductVariant variant);

    List<ProductVariantResponse> toResponses(List<ProductVariant> variants);

    ProductVariant toEntity(ProductVariantRequest request);

    void updateEntity(
            ProductVariantRequest request,
            @MappingTarget ProductVariant entity
    );
}
