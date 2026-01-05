package com.example.apigateway.controller;

import com.example.apigateway.dto.request.UserQueryRequest;
import com.example.apigateway.service.UserAggregationService;
import com.example.apigateway.dto.response.UserProfileResponse;
import com.example.commonlib.dto.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api-prefix}")
public class UsersController {

    private final UserAggregationService userAggregationService;

    @GetMapping("/users")
    public PagingResponse<UserProfileResponse> userQuery(
            @ModelAttribute UserQueryRequest request
            ) {
        return userAggregationService.userQuery(request);
    }


}

