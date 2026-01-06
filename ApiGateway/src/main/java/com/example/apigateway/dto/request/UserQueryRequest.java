package com.example.apigateway.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserQueryRequest {
    int page;
    int size;
    String keyword;
    String role;
    String sort;
    Boolean enabled;
}
