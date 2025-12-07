package com.example.profileservice.dto.request;

import com.example.profileservice.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileCreationRequest {

    @NotNull
    Integer userId;

    @Size(min = 2, max = 100)
    String fullName;

    @Email
    @Size(max = 100)
    String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$")
    String phoneNumber;

    @Past
    LocalDate dateOfBirth;

    Gender gender;

    String avatarUrl;
}
