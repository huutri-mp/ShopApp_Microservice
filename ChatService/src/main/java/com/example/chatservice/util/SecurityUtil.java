package com.example.chatservice.util;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtil {

    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        Object userIdObj = jwt.getClaim("userId");
        if (userIdObj instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    public static Integer extractUserIdFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            SignedJWT jwt = SignedJWT.parse(token);
            Object userIdObj = jwt.getJWTClaimsSet().getClaim("userId");

            if (userIdObj instanceof Number number) {
                return number.intValue();
            } else if (userIdObj instanceof String str) {
                return Integer.parseInt(str);
            }
        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String extractUsernameFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            SignedJWT jwt = SignedJWT.parse(token);
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            return claims.getSubject();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<String> extractRolesFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            SignedJWT jwt = SignedJWT.parse(token);
            Object authorities = jwt.getJWTClaimsSet().getClaim("authorities");

            if (authorities instanceof List<?>) {
                return ((List<?>) authorities).stream()
                        .map(Object::toString)
                        .map(role -> role.replace("ROLE_", ""))
                        .collect(Collectors.toList());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return List.of();
    }

}
