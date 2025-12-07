package com.example.authenticationservice.service;

import com.example.authenticationservice.dto.response.ExchangeTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FacebookIdentityService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${facebook.client-id}")
    private String clientId;

    @Value("${facebook.client-secret}")
    private String clientSecret;

    @Value("${facebook.redirect-uri}")
    private String redirectUri;

    public ExchangeTokenResponse exchangeToken(String code) {
        String url = "https://graph.facebook.com/v18.0/oauth/access_token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<ExchangeTokenResponse> response =
                    restTemplate.postForEntity(url, request, ExchangeTokenResponse.class);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw e;
        }
    }
}
