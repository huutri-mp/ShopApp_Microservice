package com.example.notificationservice.service.Impl;

import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.notificationservice.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.nio.charset.StandardCharsets;
import java.util.Map;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void send(String to, String templateName, Map<String, Object> data) {
        try {
            if (to == null || !to.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                log.error("Email không hợp lệ: {}", to);
                throw new AppException(ErrorCode.EMAIL_INVALID);
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            Context context = new Context();
            context.setVariables(data);

            String html = templateEngine.process(templateName, context);
            helper.setTo(to);
            helper.setSubject((String) data.getOrDefault("subject", "Thông báo từ hệ thống"));
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Email sent to {}", to);

        } catch (MessagingException e) {
            log.error("Lỗi gửi email đến {} với template {}: {}", to, templateName, e.getMessage(), e);
            throw new RuntimeException("Gửi email thất bại", e);
        } catch (Exception ex) {
            log.error("Lỗi không xác định khi gửi email đến {}: {}", to, ex.getMessage(), ex);
            throw new RuntimeException("Gửi email thất bại", ex);
        }
    }
}
