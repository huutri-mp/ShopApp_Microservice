package com.example.profileservice.repository;

import com.example.profileservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    Boolean existsUserProfileByEmail(String email);
    UserProfile findByEmail(String email);
}
