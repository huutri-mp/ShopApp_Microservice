package com.example.profileservice.mapper;


import com.example.profileservice.dto.request.AddressCreationRequest;
import com.example.profileservice.dto.request.AddressUpdateRequest;
import com.example.profileservice.entity.Addresses;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddress(AddressUpdateRequest request, @MappingTarget Addresses entity);
    Addresses createAddress(AddressCreationRequest request);
}

