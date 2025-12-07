package com.example.chatservice.controller;

import com.example.chatservice.constan.UrlConstant;
import com.example.chatservice.entity.Message;
import com.example.chatservice.service.ChatService;
import com.example.chatservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping(UrlConstant.API_V1_CHAT_USER)
@RequiredArgsConstructor
@CrossOrigin
public class ChatApiController {
    private final ChatService chatService;

    @GetMapping("/unread-count")
    public long getUnreadCount() {
        int userId = SecurityUtil.getCurrentUserId();
        return chatService.getUnreadCount((long)userId);
    }

    @GetMapping("/conversation/")
    public ResponseEntity<?> getConversation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        int userId = SecurityUtil.getCurrentUserId();
        List<Message> messages = chatService.getConversation(
                (long)userId,
                PageRequest.of(page, size, Sort.by("timestamp").descending())
        );
        return ResponseEntity.ok(messages);
    }
}
