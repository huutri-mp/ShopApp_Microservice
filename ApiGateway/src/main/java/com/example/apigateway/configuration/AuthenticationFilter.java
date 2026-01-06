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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationFilter implements WebFilter, Ordered {

    AuthService authService;
    ObjectMapper objectMapper;

    AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${app.api-prefix}")
    @NonFinal
    String apiPrefix;

    static List<String> PUBLIC_ALL_METHODS = List.of(
            "/auth/login",
            "/auth/logout",
            "/auth/register",
            "/auth/refresh-token",
            "/auth/outbound/authentication"
    );

    static List<String> PUBLIC_GET_ONLY = List.of(
//            "/products/**",
            "/categories/**",
            "/brands/**"
    );

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPreflight(request) || isPublic(request)) {
            return chain.filter(exchange);
        }

        return authenticate(exchange, chain);
    }

    private boolean isPreflight(ServerHttpRequest request) {
        return HttpMethod.OPTIONS.equals(request.getMethod());
    }

    private boolean isPublic(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (matchAny(path, PUBLIC_ALL_METHODS)) {
            return true;
        }

        return HttpMethod.GET.equals(method)
                && matchAny(path, PUBLIC_GET_ONLY);
    }

    private boolean matchAny(String path, List<String> patterns) {
        return patterns.stream()
                .map(p -> apiPrefix + p)
                .anyMatch(p -> pathMatcher.match(p, path));
    }

    private Mono<Void> authenticate(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractToken(exchange.getRequest());
        if (token == null) {
            return unauthenticated(exchange.getResponse(), "Missing token", HttpStatus.UNAUTHORIZED);
        }

        return authService.introspect(token)
                .flatMap(result -> {
                    if (!result.isValid()) {
                        return unauthenticated(exchange.getResponse(), "Token invalid", HttpStatus.UNAUTHORIZED);
                    }

                    try {
                        String role = extractRole(token);
                        if (isForbidden(exchange.getRequest(), role)) {
                            return unauthenticated(
                                    exchange.getResponse(),
                                    "Forbidden: Admin role required",
                                    HttpStatus.FORBIDDEN
                            );
                        }

                        return chain.filter(exchange);

                    } catch (ParseException e) {
                        return unauthenticated(exchange.getResponse(), "Invalid token", HttpStatus.UNAUTHORIZED);
                    }
                });
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(headers)) return null;

        String value = headers.get(0);
        if (!value.startsWith("Bearer ")) return null;

        return value.substring(7);
    }

    private String extractRole(String token) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(token);
        Object role = jwt.getJWTClaimsSet().getClaim("role");
        return role != null ? role.toString() : "";
    }


    private boolean isForbidden(ServerHttpRequest request, String role) {
        String path = request.getURI().getPath();
        return path.contains("/users") && !role.contains("ADMIN");
    }

    private Mono<Void> unauthenticated(ServerHttpResponse response,
                                       String message,
                                       HttpStatus status) {
        ApiResponse<?> apiResponse =
                ApiResponse.builder()
                        .code(status.value())
                        .message(message)
                        .build();

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] body = objectMapper.writeValueAsBytes(apiResponse);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }
}
