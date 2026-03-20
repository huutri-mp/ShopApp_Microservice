package com.example.profileservice.controller;

import com.example.commonlib.dto.ApiResponse;
import com.example.profileservice.constant.UrlConstant;
import com.example.profileservice.dto.request.ProfileUpdateRequest;
import com.example.profileservice.dto.response.UserProfileResponse;
import com.example.profileservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_PROFILE)
public class UserProfileController {

    private final UserProfileService userProfileService;


    @GetMapping("/myInfo")
    public ApiResponse<UserProfileResponse> getMyInfo() {
        ApiResponse apiResponse = new ApiResponse();
        UserProfileResponse userProfileResponse = userProfileService.getMyInfo();
        apiResponse.setData(userProfileResponse);
        return apiResponse;
    }

    @PutMapping("/update/{userId}")
    public ApiResponse<UserProfileResponse> updateProfile(
            @PathVariable Integer userId,
            @RequestPart (value = "profileUpdate", required = false) ProfileUpdateRequest request,
            @RequestPart(value = "avt", required = false) MultipartFile avt) {
        ApiResponse apiResponse = new ApiResponse();
        UserProfileResponse userProfileResponse = userProfileService.updateUserProfile(userId,request, avt);
        apiResponse.setData(userProfileResponse);
        return apiResponse;
    }


    @DeleteMapping("/delete/{userId}")
    public ApiResponse<String> deleteUserProfile(@PathVariable Integer userId) {
        ApiResponse apiResponse = new ApiResponse();
        String response = userProfileService.deleteUserProfile(userId);
        apiResponse.setData(response);
        return apiResponse;
    }
}