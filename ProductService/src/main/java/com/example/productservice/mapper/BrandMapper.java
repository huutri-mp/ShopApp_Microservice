package com.example.productservice.mapper;

import com.example.productservice.dto.response.BrandResponse;
import com.example.productservice.entity.Brand;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    BrandResponse toResponse(Brand brand);

}