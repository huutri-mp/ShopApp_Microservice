package com.example.authenticationservice.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class InvalidatedToken {
    @Id
    String id;

    @Column(name = "expiry_time", nullable = false)
    Date expiryTime;
}

