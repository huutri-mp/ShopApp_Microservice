package com.example.commonlib.feign;

import com.example.commonlib.dto.ApiResponse;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            ApiResponse<?> apiResponse = new ObjectMapper().readValue(
                    response.body().asInputStream(),
                    ApiResponse.class
            );

            return new AppException(ErrorCode.BAD_REQUEST);

        } catch (IOException e) {
            return new AppException(ErrorCode.INTERNAL_ERROR);
        }
    }
}