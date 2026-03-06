package com.example.notificationservice.service;

import com.example.commonlib.Enum.MailTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MailService {
    void send(String to, MailTemplate mailTemplate, Map<String, Object> variables);
}
