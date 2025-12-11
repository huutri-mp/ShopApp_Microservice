package com.example.apigateway.service;

import auth.Auth;
import auth.AuthServiceGrpc;
import com.example.apigateway.dto.response.IntrospectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthServiceGrpc.AuthServiceBlockingStub authServiceStub;

    public Mono<IntrospectResponse> introspect(String token) {
        return Mono.fromCallable(() -> {
            Auth.IntrospectRequest grpcRequest = Auth.IntrospectRequest.newBuilder()
                    .setToken(token)
                    .build();

            Auth.IntrospectResponse grpcResponse = authServiceStub.introspect(grpcRequest);
            return IntrospectResponse.builder()
                    .valid(grpcResponse.getValid())
                    .build();
        });
    }

}


