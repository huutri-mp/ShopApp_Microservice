package com.example.apigateway.configuration;

import auth.AuthServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import profile.ProfileServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class Config {

    @Bean
    public ManagedChannel authServiceChannel(
            @Value("${auth.service.grpc.host}") String host,
            @Value("${auth.service.grpc.port}") int port) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

        @Bean
        public ManagedChannel profileServiceChannel(
            @Value("${profile.service.grpc.host}") String host,
            @Value("${profile.service.grpc.port}") int port) {
        return ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build();
        }

    @Bean
    public AuthServiceGrpc.AuthServiceBlockingStub authServiceStub(ManagedChannel authServiceChannel) {
        return AuthServiceGrpc.newBlockingStub(authServiceChannel);
    }

    @Bean
    public ProfileServiceGrpc.ProfileServiceBlockingStub profileServiceStub(ManagedChannel profileServiceChannel) {
        return ProfileServiceGrpc.newBlockingStub(profileServiceChannel);
    }
}



