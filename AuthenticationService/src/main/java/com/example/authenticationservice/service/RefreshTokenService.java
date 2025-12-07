package com.example.authenticationservice.service;


import com.example.authenticationservice.entity.AuthenUser;
import com.example.authenticationservice.entity.RefreshToken;
import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
    String creatRefreshToken(AuthenUser user);
    RefreshToken getRefreshToken(String token);
    void revokeToken (String token);
}
