package com.example.apigateway.repository;

import com.example.apigateway.dto.request.IntrospectRequest;
import com.example.apigateway.dto.response.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestHeader;

public interface AuthClient {

    @PostExchange(
            url = "/introspect",
            contentType = MediaType.APPLICATION_JSON_VALUE
    )
    Mono<IntrospectResponse> introspect( @RequestBody IntrospectRequest request,
                                         @RequestHeader("Authorization") String authorizationHeader);

}
