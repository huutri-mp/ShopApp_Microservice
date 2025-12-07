package com.example.authenticationservice.controller;

import com.example.authenticationservice.constan.UrlConstan;
import com.example.authenticationservice.dto.request.*;
import com.example.authenticationservice.dto.response.IntrospectResponse;
import com.example.authenticationservice.dto.response.LoginResponse;
import com.example.authenticationservice.entity.AuthenUser;
import com.example.authenticationservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UrlConstan.API_V1_AUTH_INTERNAL)
public class InternalAuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/introspect")
    public ResponseEntity<IntrospectResponse> introspect(
            @RequestBody IntrospectRequest request,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authService.introspect(request));
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        authService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}


