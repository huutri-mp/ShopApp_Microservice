package com.example.authenticationservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String userName;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 255)
    String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email khong hop le")
    String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 9, max = 12)
    String phoneNumber;

    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    String fullName;

    LocalDate dateOfBirth;

    @NotBlank(message = "Gender cannot be blank")
    String gender;

    String role;
}
