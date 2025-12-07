package com.example.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "authen_user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "username")
    String userName;

    @Column(name = "password", nullable = true)
    String password;
    String role;

    Boolean enabled = true;

    @Column(name = "last_password_change")
    LocalDate lastPasswordChange;

    @OneToMany(mappedBy = "authenUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<LoginHistory> loginHistories;

}
