package com.example.apigateway.configuration;

import com.example.commonlib.dto.ApiResponse;
import com.example.apigateway.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {

    AuthService authService;
    ObjectMapper objectMapper;

    @NonFinal
    String[] publicUrls = {
            "/auth/login",
            "/auth/logout",
            "/auth/register",
            "/auth/outbound/authentication",
            "/auth/refresh-token",
            "/user/product",
            "/user/product/price-range",
            "/user/product/{productId}",
            "/user/category",
            "/user/category/{categoryId}",
            "/search",
    };

    @Value("${app.api-prefix}")
    @NonFinal
    String apiRefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Authentication Filter");
        if (isPublicUrl(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader) || authHeader.get(0) == null || authHeader.get(0).isBlank()) {
            return unauthenticated(exchange.getResponse());
        }

        String token = authHeader.get(0).replace("Bearer ", "");

        return authService.introspect(token)
                .flatMap(response -> {
                    log.info("Authentication Success: {}", response.isValid());
                    if (response.isValid()) {
                        return chain.filter(exchange);
                    } else {
                        return unauthenticated(exchange.getResponse());
                    }
                })
                .onErrorResume(error -> {
                    log.error("Token introspect failed", error);
                    return unauthenticated(exchange.getResponse());
                });
    }

    private boolean isPublicUrl(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return Arrays.stream(publicUrls)
                .map(url -> apiRefix + url)
                .anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")
                .build();

        String body;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }
}