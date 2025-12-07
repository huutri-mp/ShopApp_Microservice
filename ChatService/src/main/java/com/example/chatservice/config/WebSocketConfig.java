package com.example.chatservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/v1/chat/ws-chat")
                .setAllowedOriginPatterns("*") // Cho phép tất cả các origin
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Route từ client gửi tới @MessageMapping
        registry.setApplicationDestinationPrefixes("/api/v1/chat/app");

        // Route từ server gửi ngược về client
        registry.enableSimpleBroker("/api/v1/chat/topic", "/api/v1/chat/queue");

        // Dành cho convertAndSendToUser(...)
        registry.setUserDestinationPrefix("/api/v1/chat/user");
    }
}
