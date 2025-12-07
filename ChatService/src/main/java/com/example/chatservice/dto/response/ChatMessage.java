package com.example.chatservice.dto.response;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long receiverId;
    private String content;
    private Instant timestamp;
    private String senderRole;

    // Thêm constructor phù hợp với mapper
    public ChatMessage(Long senderId, Long receiverId, String content, Instant timestamp) {
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }
}