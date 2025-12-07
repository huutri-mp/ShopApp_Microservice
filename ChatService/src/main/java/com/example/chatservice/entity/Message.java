package com.example.chatservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Message {
    @Id
    private String id;
    private Long senderId;
    private String senderRole;
    private Long receiverId;
    private String content;
    private Instant timestamp;
    private MessageStatus status;

    public enum MessageStatus {
        SENT, DELIVERED, READ
    }
}

