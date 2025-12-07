package com.example.notificationservice.controller;

import com.example.commonlib.dto.NotificationEvent;
import com.example.notificationservice.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<String> userCreated(@RequestBody NotificationEvent request) {
        log.info("Received notification message: {}", request);
        mailService.send(request.getRecipient(), request.getTemplate(), request.getData());
        return ResponseEntity.ok("Notification sent successfully");
    }

}