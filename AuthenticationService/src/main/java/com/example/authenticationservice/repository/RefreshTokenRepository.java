package com.example.authenticationservice.repository;

import com.example.authenticationservice.entity.RefreshToken;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken getRefreshTokenByToken(String token);
    @Modifying
    @Query("""
    DELETE FROM RefreshToken t
    WHERE t.expiresAt < :now OR t.isValid = false
""")
    void deleteInvalidTokens(@Param("now") Date now);
}
