package com.example.authenticationservice.repository;

import com.example.authenticationservice.entity.AuthenUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<AuthenUser, Integer> {
    Boolean existsUserByUserName(String userName);
    AuthenUser findByUserName(String userName);
    AuthenUser getUserById(int id);
}
