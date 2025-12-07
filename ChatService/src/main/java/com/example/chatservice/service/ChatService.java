package com.example.chatservice.service;

import com.example.chatservice.dto.response.ChatMessage;
import com.example.chatservice.dto.response.UserProfileResponseInternal;
import com.example.chatservice.entity.Message;
import com.example.chatservice.repository.MessageRepository;
import com.example.chatservice.repository.httpClient.ProfileClient;
import com.example.event.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final ProfileClient profileClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Transactional
    public Message sendMessage(ChatMessage message, int senderId, List<String> roles) {
        String senderRole = roles.contains("ADMIN") ? "ADMIN" : "USER";

        Message newMessage = Message.builder()
                .senderId((long) senderId)
                .senderRole(senderRole)
                .receiverId("ADMIN".equals(senderRole) ? message.getReceiverId() : null)
                .content(message.getContent())
                .timestamp(Instant.now())
                .status(Message.MessageStatus.SENT)
                .build();

        Message saved = messageRepository.save(newMessage);

        // Gửi tới user nếu người gửi là admin
        if ("ADMIN".equals(senderRole)) {
            saved.setStatus(Message.MessageStatus.DELIVERED);
            messageRepository.save(saved);
            UserProfileResponseInternal userProfileResponseInternal = profileClient.getProfile(senderId).getBody();

            messagingTemplate.convertAndSendToUser(
                    message.getReceiverId().toString(),
                    "/queue/private-messages",
                    saved
            );

            Map<String, Object> data = new HashMap<>();
            data.put("fullName", userProfileResponseInternal.getFullName());
            data.put("fromUser", "ADMIN");
            data.put("messagePreview", message.getContent());

            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .channel("email")
                    .recipient(userProfileResponseInternal.getEmail())
                    .template("new_message")
                    .data(data)
                    .build();
            try {
                kafkaTemplate.send("notification-delivery", notificationEvent);
            } catch (Exception e) {
                log.error("Failed to send Kafka notification event", e);
            }

        }
        else {

            // USER gửi → Gửi broadcast tới tất cả admin
            messagingTemplate.convertAndSend("/topic/admin", saved);
        }

        log.info("Message from {} saved: {}", senderRole, saved);
        return saved;
    }

    public long getUnreadCount(Long userId) {
        return messageRepository.countByReceiverIdAndStatus(userId, Message.MessageStatus.DELIVERED);
    }

    // Đánh dấu tin nhắn đã đọc
    public Message markAsRead(String messageId, Long readerId) {
        return messageRepository.findById(messageId)
                .map(message -> {
                    if (message.getStatus() != Message.MessageStatus.READ) {
                        if (readerId.equals(message.getReceiverId())) {
                            message.setStatus(Message.MessageStatus.READ);
                            Message updated = messageRepository.save(message);

                            // Thông báo real-time cho sender
                            messagingTemplate.convertAndSendToUser(
                                    message.getSenderId().toString(),
                                    "/queue/read-receipts",
                                    Map.of(
                                            "messageId", messageId,
                                            "readAt", Instant.now()
                                    )
                            );
                            return updated;
                        }
                        throw new SecurityException("Unauthorized read operation");
                    }
                    return message;
                })
                .orElseThrow(() -> new Error(messageId));
    }
        public List<Message> getConversation(Long userId, Pageable pageable) {
            return messageRepository.findConversation(userId, pageable);
        }

}
