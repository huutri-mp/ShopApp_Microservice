package com.example.profileservice.dto.response;

import com.example.profileservice.entity.Addresses;
import com.example.profileservice.enums.Gender;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.management.relation.Role;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserProfileResponse {
    int userId;
    String username;
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
