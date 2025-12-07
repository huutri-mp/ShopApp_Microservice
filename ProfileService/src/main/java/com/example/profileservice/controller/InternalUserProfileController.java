package com.example.profileservice.controller;

import com.example.profileservice.constan.UrlConstant;
import com.example.profileservice.dto.request.ProfileCreationRequest;
import com.example.profileservice.dto.response.UserProfileResponseInternal;
import com.example.profileservice.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(UrlConstant.API_V1_PROFILE_INTERNAL)
public class InternalUserProfileController {
    @Autowired
    private UserProfileService userProfileService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createProfile(@RequestBody @Validated ProfileCreationRequest request){
        log.info("Received request to create profile");
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfileService.createUserProfile(request));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        log.info("Checking if email exists: {}", email);
        boolean exists = userProfileService.checkEmailExists(email);
        log.info("Email {} exists: {}", email, exists);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/get-profile")
    public ResponseEntity<UserProfileResponseInternal> getProfile(@RequestParam int userId) {
        UserProfileResponseInternal response = userProfileService.getProfileById(userId);
        if (response == null) {
            log.error("Profile not found for userId: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.info("Profile found for userId: {}", userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-profile-email")
    public ResponseEntity<UserProfileResponseInternal> getProfile(@RequestParam String email) {
        log.info("Fetching profile by email: {}", email);
        UserProfileResponseInternal response = userProfileService.getProfileByEmail(email);
        if (response == null) {
            log.error("Profile not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.info("Profile found for email: {}", email);
        return ResponseEntity.ok(response);
    }

}
