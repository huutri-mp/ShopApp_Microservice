package com.example.authenticationservice.repository.httpClient;

import com.example.authenticationservice.dto.request.ProfileCreationRequest;
import com.example.authenticationservice.dto.response.UserProfileResponseInternal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "profile-service", url = "${app.services.profile}")
public interface ProfileClient {

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> createProfile(@RequestBody ProfileCreationRequest request);

    @GetMapping("/check-email")
    ResponseEntity<Boolean> checkEmailExists(@RequestParam("email") String email);

    @GetMapping("/get-profile")
    ResponseEntity<UserProfileResponseInternal> getProfile(@RequestParam("userId") int userId);


    @GetMapping("/get-profile-email")
    ResponseEntity<UserProfileResponseInternal> getProfileByEmail(@RequestParam("email") String email);

}
