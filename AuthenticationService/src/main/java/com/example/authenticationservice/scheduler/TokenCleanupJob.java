package com.example.authenticationservice.scheduler;

import com.example.authenticationservice.repository.InvalidatedTokenRepository;
import com.example.authenticationservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupJob {

    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 * * * *") // mỗi 1 giờ
    public void cleanExpiredTokens() {
        invalidatedTokenRepository.deleteByExpiryTimeBefore(new Date());
        log.info("Expired tokens have been deleted.");
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanRefreshTokens() {
        refreshTokenRepository.deleteInvalidTokens(new Date());
        log.info("Refresh tokens have been deleted.");
    }
}