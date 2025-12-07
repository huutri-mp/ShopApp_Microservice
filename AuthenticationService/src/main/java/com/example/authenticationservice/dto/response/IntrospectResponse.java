package com.example.authenticationservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class IntrospectResponse {
    private boolean isValid;
}

