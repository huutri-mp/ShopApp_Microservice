package com.example.authenticationservice.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponseInternal {
    private int userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;

    UserProfileResponseInternal(int userId, String fullName, String email, String phoneNumber, String gender, LocalDate dateOfBirth) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public static UserProfileResponseInternalBuilder builder() {
        return new UserProfileResponseInternalBuilder();
    }

    public static class UserProfileResponseInternalBuilder {
        private int userId;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String gender;
        private LocalDate dateOfBirth;

        UserProfileResponseInternalBuilder() {
        }

        public UserProfileResponseInternalBuilder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public UserProfileResponseInternalBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserProfileResponseInternalBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserProfileResponseInternalBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserProfileResponseInternalBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public UserProfileResponseInternalBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public UserProfileResponseInternal build() {
            return new UserProfileResponseInternal(this.userId, this.fullName, this.email, this.phoneNumber, this.gender, this.dateOfBirth);
        }

        public String toString() {
            return "UserProfileResponseInternal.UserProfileResponseInternalBuilder(userId=" + this.userId + ", fullName=" + this.fullName + ", email=" + this.email + ", phoneNumber=" + this.phoneNumber + ", gender=" + this.gender + ", dateOfBirth=" + this.dateOfBirth + ")";
        }
    }
}
