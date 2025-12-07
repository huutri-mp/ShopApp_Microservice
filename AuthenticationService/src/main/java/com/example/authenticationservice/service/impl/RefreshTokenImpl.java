package com.example.authenticationservice.service.impl;

import com.example.authenticationservice.entity.AuthenUser;
import com.example.authenticationservice.entity.RefreshToken;
import com.example.authenticationservice.repository.RefreshTokenRepository;
import com.example.authenticationservice.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@Primary
public class RefreshTokenImpl implements RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.refresh-valid-duration}")
    private long REFRESH_VALID_DURATION;

    public String creatRefreshToken(AuthenUser user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiresAt(new Date(System.currentTimeMillis() + REFRESH_VALID_DURATION ))
                .token(UUID.randomUUID().toString())
                .isValid(true)
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public RefreshToken getRefreshToken (String token){
        RefreshToken refreshToken  = refreshTokenRepository.getRefreshTokenByToken(token);
        return refreshToken;
    }

    public void revokeToken (String token){
        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByToken(token);
        if (refreshToken == null)
            return;

        refreshToken.setIsValid(false);
        refreshToken.setRevokedAt(new Date(0));
        refreshTokenRepository.save(refreshToken);
    }
}
