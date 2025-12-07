package com.example.profileservice.dto.request;

import com.example.profileservice.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileUpdateRequest {
    @Size(min = 2, max = 100)
    String fullName;

    @Email
    String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$")
    String phoneNumber;

    @Past
    LocalDate dateOfBirth;

    Gender gender;

}

