package com.example.profileservice.config;


import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Extract authorities from claims
            List<String> authorities = extractAuthorities(claimsSet);

            return new Jwt(
                    token,
                    claimsSet.getIssueTime() != null ? claimsSet.getIssueTime().toInstant() : null,
                    claimsSet.getExpirationTime() != null ? claimsSet.getExpirationTime().toInstant() : null,
                    signedJWT.getHeader().toJSONObject(),
                    convertClaims(claimsSet, authorities)
            );
        } catch (ParseException e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new JwtException("Failed to process JWT token: " + e.getMessage(), e);
        }
    }

    private List<String> extractAuthorities(JWTClaimsSet claimsSet) {
        try {
            Object authoritiesClaim = claimsSet.getClaim("authorities");
            if (authoritiesClaim instanceof List) {
                return ((List<?>) authoritiesClaim).stream()
                        .map(Object::toString)
                        .filter(role -> !role.isEmpty())
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Map<String, Object> convertClaims(JWTClaimsSet claimsSet, List<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.putAll(claimsSet.getClaims());
        if (!authorities.isEmpty()) {
            claims.put("authorities", authorities);
        }
        return claims;
    }
}