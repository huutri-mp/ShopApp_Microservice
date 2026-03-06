package com.example.notificationservice.config;

import com.mailjet.client.MailjetClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MailjetConfig {

    @Value("${mailjet.apiKey}")
    private String apiKey;

    @Value("${mailjet.secretKey}")
    private String secretKey;

    @Bean
    public MailjetClient mailjetClient() {
        return new MailjetClient(apiKey, secretKey);
    }
}
