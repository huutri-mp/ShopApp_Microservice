package com.example.notificationservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MailService {
    void send(String to, String templateName, Map<String, Object> data);
}
