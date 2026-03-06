package com.example.notificationservice.service.Impl;

import com.example.commonlib.Enum.MailTemplate;
import com.example.notificationservice.service.MailService;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


import java.util.Map;

@Primary
@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final MailjetClient mailjetClient;

    @Value("${mailjet.template.ordersuccess}")
    private String ordersuccess;

    @Value("${mailjet.template.passwordchanged}")
    private String passwordchanged;

    @Value("${mailjet.template.usercreated}")
    private String usercreated;

    @Value("${mailjet.template.paymentsuccess}")
    private String paymentsuccess;

    @Value("${mailjet.template.userupdated}")
    private String userupdated;

    @Value("${mailjet.template.ordercancelled}")
    private String ordercancelled;


    @Override
    public void send(
            String to,
            MailTemplate template,
            Map<String, Object> data
    ) {
        long templateId;
        switch (template) {
            case ORDER_SUCCESS -> templateId = Long.parseLong(ordersuccess);
            case PASSWORD_CHANGED -> templateId = Long.parseLong(passwordchanged);
            case USER_CREATED -> templateId = Long.parseLong(usercreated);
            case PAYMENT_SUCCESS -> templateId = Long.parseLong(paymentsuccess);
            case USER_UPDATED -> templateId = Long.parseLong(userupdated);
            case ORDER_CANCLE -> templateId = Long.parseLong(ordercancelled);
            default -> throw new IllegalArgumentException("Unknown template");
        }

        try {
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject().put("Email", to)))
                                    .put(Emailv31.Message.TEMPLATEID, templateId)
                                    .put(Emailv31.Message.TEMPLATELANGUAGE, true)
                                    .put(Emailv31.Message.VARIABLES,
                                            new JSONObject(data))
                            ));

            MailjetResponse response = mailjetClient.post(request);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Mailjet send failed");
            }

        } catch (Exception e) {
            log.error("Send mail failed. to={}, template={}", to, template, e);
        }
    }

}
