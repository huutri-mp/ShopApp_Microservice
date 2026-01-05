package com.example.apigateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class UserProfileResponse {
    Integer userId;
    String userName;
    String fullName;
    String avatar;
    String email;
    String phoneNumber;
    Gender gender;
    LocalDate dateOfBirth;
    String role;
    Boolean enabled;
    Boolean needsPasswordCreation;
    List<Addresses> addresses;
}
