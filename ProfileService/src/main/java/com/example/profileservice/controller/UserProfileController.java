package com.example.profileservice.controller;

import com.example.commonlib.dto.ApiResponse;
import com.example.profileservice.constan.UrlConstant;
import com.example.profileservice.dto.request.ProfileUpdateRequest;
import com.example.profileservice.dto.response.UserProfileResponse;
import com.example.profileservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_PROFILE_USER)
public class UserProfileController {

    private final UserProfileService userProfileService;


    @GetMapping("/myInfo")
    public ApiResponse<UserProfileResponse> getMyInfo() {
        ApiResponse apiResponse = new ApiResponse();
        UserProfileResponse userProfileResponse = userProfileService.getMyInfo();
        System.out.println(userProfileResponse.toString());
        apiResponse.setData(userProfileResponse);
        return apiResponse;
    }

    @PutMapping("/update")
    public ApiResponse<UserProfileResponse> updateProfile(
            @RequestPart (value = "profileUpdate", required = false) ProfileUpdateRequest request,
            @RequestPart(value = "avt", required = false) MultipartFile avt) {
        ApiResponse apiResponse = new ApiResponse();
        UserProfileResponse userProfileResponse = userProfileService.updateUserProfile(request, avt);
        apiResponse.setData(userProfileResponse);
        return apiResponse;
    }

    @PreAuthorize("authentication.principal.claims['role'] == 'ADMIN'")
    @GetMapping("/all")
    public ApiResponse<List<UserProfileResponse>> getAllUserProfiles() {
        ApiResponse apiResponse = new ApiResponse();
        List<UserProfileResponse> userProfileResponses = userProfileService.getAllUserProfiles();
        apiResponse.setData(userProfileResponses);
        return apiResponse;
    }

    @PreAuthorize("authentication.principal.claims['role'] == 'ADMIN' ")
    @DeleteMapping("/delete/{userId}")
    public ApiResponse<String> deleteUserProfile(@PathVariable Integer userId) {
        ApiResponse apiResponse = new ApiResponse();
        String response = userProfileService.deleteUserProfile(userId);
        apiResponse.setData(response);
        return apiResponse;
    }
}