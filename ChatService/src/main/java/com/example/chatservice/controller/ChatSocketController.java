package com.example.chatservice.controller;

import com.example.chatservice.constan.UrlConstant;
import com.example.chatservice.dto.response.ChatMessage;
import com.example.chatservice.entity.Message;
import com.example.chatservice.service.ChatService;
import com.example.chatservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(UrlConstant.API_V1_CHAT_USER)
@RequiredArgsConstructor
public class ChatSocketController {
    private final ChatService chatService;

    @MessageMapping("/send")
    public void handleMessage(@Payload ChatMessage message,
                              SimpMessageHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        log .info("Received message: {}", token);
        if (token == null || token.isEmpty()) {
            log.warn("Missing Authorization header");
            return;
        }

        int senderId = SecurityUtil.extractUserIdFromToken(token);
        List<String> roles = SecurityUtil.extractRolesFromToken(token);

        chatService.sendMessage(message, senderId, roles);
    }


    @MessageMapping("/mark-as-read")
    public void handleMarkAsRead(
            @Payload String messageId,
            SimpMessageHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token == null || token.isEmpty()) {
            log.warn("Missing Authorization header");
            return;
        }

        int senderId = SecurityUtil.extractUserIdFromToken(token);
        List<String> roles = SecurityUtil.extractRolesFromToken(token);
        log.info(token + " - Sender ID: " + senderId + ", Roles: " + roles);
        Message message = chatService.markAsRead(messageId, (long) senderId);
    }
}