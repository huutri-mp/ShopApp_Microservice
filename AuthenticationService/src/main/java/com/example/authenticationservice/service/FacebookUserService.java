package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.response.OutboundUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FacebookUserService {

    private final RestTemplate restTemplate = new RestTemplate();

    public OutboundUserResponse getUserInfo(String accessToken) {
        String fields = "name,email,picture";

        String url = String.format(
                "https://graph.facebook.com/me?fields=%s&access_token=%s",
                fields, accessToken
        );

        ResponseEntity<OutboundUserResponse> response =
                restTemplate.getForEntity(url, OutboundUserResponse.class);

        return response.getBody();
    }
}

