package com.example.authenticationservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileCreationRequest {
    int userId;
    String fullName;
    String email;
    String phoneNumber;
    LocalDate dateOfBirth;
    String gender;
    String avatarUrl;
}
