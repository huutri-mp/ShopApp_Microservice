package com.example.orderservice.repository.httpClient;

import com.example.orderservice.dto.response.UserProfileResponseInternal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "profile-service", url = "${app.services.profile}")
public interface ProfileClient {

    @GetMapping("/get-profile")
    ResponseEntity<UserProfileResponseInternal> getProfile(@RequestParam("userId") int userId);


    @GetMapping("/get-profile-email")
    ResponseEntity<UserProfileResponseInternal> getProfileByEmail(@RequestParam("email") String email);

}

