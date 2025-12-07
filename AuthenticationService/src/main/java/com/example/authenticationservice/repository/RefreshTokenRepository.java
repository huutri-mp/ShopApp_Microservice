package com.example.authenticationservice.repository;

import com.example.authenticationservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken getRefreshTokenByToken(String token);
}
