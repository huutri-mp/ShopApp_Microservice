package com.example.apigateway.configuration;

import com.example.commonlib.dto.ApiResponse;
import com.example.apigateway.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements WebFilter, Ordered {
    AuthService authService;
    ObjectMapper objectMapper;

    @NonFinal
    String[] publicUrls = {
            "/auth/login",
            "/auth/logout",
            "/auth/register",
            "/auth/refresh-token",
            "/auth/outbound/authentication",

    };

    @NonFinal
    String[] publicGetOnlyUrls = {
            "/products/**",
            "/categories/**",
            "/brands/**",
    };

    @Value("${app.api-prefix}")
    @NonFinal
    String apiRefix;

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        if (isPublicUrl(request)) {
            return chain.filter(exchange);
        }



        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader) || !authHeader.get(0).startsWith("Bearer ")) {
            return unauthenticated(exchange.getResponse(), "Missing token", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.get(0).replace("Bearer ", "");

        return authService.introspect(token)
                .flatMap(response -> {
                    if (response.isValid()) {
                        try {
                            String role = getRoleFromToken(token);
                            if (path.contains("/users") && !role.contains("ADMIN")) {
                                return unauthenticated(exchange.getResponse(), "Forbidden: Admin role required", HttpStatus.FORBIDDEN);
                            }
                            return chain.filter(exchange);

                        } catch (ParseException e) {
                            return unauthenticated(exchange.getResponse(), "Invalid token format", HttpStatus.UNAUTHORIZED);
                        }
                    } else {
                        return unauthenticated(exchange.getResponse(), "Token invalid", HttpStatus.UNAUTHORIZED);
                    }
                });
    }

    private String getRoleFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Object role = signedJWT.getJWTClaimsSet().getClaim("role");
        return role != null ? role.toString() : "";
    }

    private boolean isPublicUrl(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // Public cho mọi method
        boolean isPublic =
                Arrays.stream(publicUrls)
                        .map(url -> apiRefix + url)
                        .anyMatch(path::startsWith);

        if (isPublic) return true;

        // Chỉ public khi GET
        if (HttpMethod.GET.equals(method)) {
            return Arrays.stream(publicGetOnlyUrls)
                    .map(url -> apiRefix + url)
                    .anyMatch(path::startsWith);
        }

        return false;
    }


    public Mono<Void> unauthenticated(ServerHttpResponse response, String message, HttpStatus status) {
        ApiResponse<?> apiResponse = ApiResponse.builder().code(status.value()).message(message).build();
        response.setStatusCode(status);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        try {
            String body = objectMapper.writeValueAsString(apiResponse);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }
}