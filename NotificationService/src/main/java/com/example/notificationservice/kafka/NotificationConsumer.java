package com.example.notificationservice.kafka;

import com.example.notificationservice.service.MailService;
import com.example.commonlib.dto.NotificationEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationConsumer {

    private final MailService mailService;

    @KafkaListener(topics = "notification-delivery", groupId = "notification-service")
    public void handleNotification(NotificationEvent request) {
        log.info("Received notification message: {}", request);
        mailService.send(request.getRecipient(), request.getTemplate(), request.getData());
    }
}
