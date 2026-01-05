package com.example.profileservice.repository;

import com.example.profileservice.entity.UserProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer>,
                                                JpaSpecificationExecutor<UserProfile> {
    @EntityGraph(attributePaths = "addresses")
    Optional<UserProfile> findById(Integer id);
    Boolean existsUserProfileByEmail(String email);
    UserProfile findByEmail(String email);
}
