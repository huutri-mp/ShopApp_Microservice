package com.example.paymentservce.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserProfileResponseInternal {
    int userId;
    String fullName;
    String email;
    String phoneNumber;
    String gender;
    LocalDate dateOfBirth;
}
