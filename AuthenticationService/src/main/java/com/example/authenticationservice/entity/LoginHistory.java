package com.example.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "login_history")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    AuthenUser authenUser;

    String status;
    @Column(name = "ip_address")
    String ipAddress;
    @Column(name = "user_agent")
    String userAgent;
    @Column(name = "login_At", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date loginAt;
}
