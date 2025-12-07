package com.example.apigateway.service;

import com.example.apigateway.dto.request.IntrospectRequest;
import com.example.apigateway.dto.response.IntrospectResponse;
import com.example.apigateway.repository.AuthClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthClient authClient;

    @Value("${auth.username}")
    private String username;

    @Value("${auth.password}")
    private String password;

    public Mono<IntrospectResponse> introspect(String token) {
        String credentials = username + ":" + password;
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        return authClient.introspect(
                IntrospectRequest.builder().token(token).build(),
                authHeader
        );
    }

}


