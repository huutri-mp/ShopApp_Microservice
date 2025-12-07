package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.response.OutboundUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleUserService {

    private final RestTemplate restTemplate = new RestTemplate();

    public OutboundUserResponse getUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<OutboundUserResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                OutboundUserResponse.class
        );

        return response.getBody();
    }
}
