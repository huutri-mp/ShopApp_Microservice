package com.example.profileservice.mapper;

import com.example.profileservice.dto.request.ProfileCreationRequest;
import com.example.profileservice.dto.request.ProfileUpdateRequest;
import com.example.profileservice.dto.response.UserProfileResponse;
import com.example.profileservice.entity.UserProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfile(ProfileUpdateRequest request, @MappingTarget UserProfile entity);
    UserProfile createProfile(ProfileCreationRequest request);
}
