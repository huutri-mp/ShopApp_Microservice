package com.example.profileservice.service;

import com.example.commonlib.dto.PagingResponse;
import com.example.profileservice.dto.request.ProfileCreationRequest;
import com.example.profileservice.dto.request.ProfileUpdateRequest;
import com.example.profileservice.dto.response.UserProfileResponse;
import com.example.profileservice.dto.response.UserProfileResponseInternal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserProfileService {
    String createUserProfile(ProfileCreationRequest request);
    boolean checkEmailExists(String email);
    UserProfileResponse getMyInfo();
    UserProfileResponse updateUserProfile(int userId ,ProfileUpdateRequest request, MultipartFile avatar);
    List<UserProfileResponse> getUserProfilesInternal(String keyword);
    String deleteUserProfile(Integer userId);
    UserProfileResponseInternal getProfileById(int userId);
    UserProfileResponseInternal getProfileByEmail(String email);
}
