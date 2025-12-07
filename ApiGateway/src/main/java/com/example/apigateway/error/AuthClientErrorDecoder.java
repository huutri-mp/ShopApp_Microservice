package com.example.apigateway.error;

import feign.Response;
import feign.codec.ErrorDecoder;

public class AuthClientErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 401) {
            throw  new RuntimeException("Invalid service credentials");
        }
        if (response.status() == 403) {
            throw  new RuntimeException("Service not authorized");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
