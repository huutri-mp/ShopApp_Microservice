package com.example.profileservice.service.Impl;

import com.example.commonlib.dto.NotificationEvent;
import com.example.commonlib.dto.PagingResponse;
import com.example.profileservice.dto.request.ProfileCreationRequest;
import com.example.profileservice.dto.request.ProfileUpdateRequest;
import com.example.profileservice.dto.response.UserProfileResponse;
import com.example.profileservice.dto.response.UserProfileResponseInternal;
import com.example.profileservice.entity.UserProfile;
import com.example.profileservice.mapper.ProfileMapper;
import com.example.profileservice.repository.UserProfileRepository;
import com.example.profileservice.repository.gRPC.AuthGrpcClient;
import com.example.profileservice.repository.gRPC.UploadGrpcClient;
import com.example.profileservice.service.UserProfileService;
import com.example.profileservice.util.SecurityUtil;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    @Value("${azure.storage.container-name}")
    private String containerName;

    private final UserProfileRepository userProfileRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UploadGrpcClient uploadClient;
    private final AuthGrpcClient authGrpcClient;
    private final ProfileMapper profileMapper;

    public UserProfileResponseInternal getProfileById (int userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        return UserProfileResponseInternal.builder()
                .userId(userProfile.getId())
                .fullName(userProfile.getFullName())
                .email(userProfile.getEmail())
                .phoneNumber(userProfile.getPhoneNumber())
                .dateOfBirth(userProfile.getDateOfBirth())
                .gender(userProfile.getGender())
                .build();
    }

    public UserProfileResponseInternal getProfileByEmail(String email) {
            UserProfile userProfile = userProfileRepository.findByEmail(email);
            if (userProfile == null) {
                throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            }
        return UserProfileResponseInternal.builder()
                .userId(userProfile.getId())
                .fullName(userProfile.getFullName())
                .email(userProfile.getEmail())
                .phoneNumber(userProfile.getPhoneNumber())
                .dateOfBirth(userProfile.getDateOfBirth())
                .gender(userProfile.getGender())
                .build();
    }
    public String createUserProfile(ProfileCreationRequest request) {
        if(request == null) {
            throw new AppException(ErrorCode.INVALID_PROFILE_DATA);
        }

        if (userProfileRepository.existsUserProfileByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserProfile userProfile = profileMapper.createProfile(request);

        try {
            userProfileRepository.save(userProfile);
            log.info("Created profile for user ID: {}", request.getUserId());
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.PROFILE_CREATION_FAILED);
        }

        //kafka
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", request.getFullName());
        data.put("registerTime", LocalDate.now());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("email")
                .recipient(request.getEmail())
                .template("user_created")
                .data(data)
                .build();
        try {
            kafkaTemplate.send("notification-delivery", notificationEvent);
        } catch (Exception e) {
            log.error("Failed to send Kafka notification event", e);
        }
        return "User profile created successfully";
    }

    public boolean checkEmailExists(String email) {
        return userProfileRepository.existsUserProfileByEmail(email);
    }

    public UserProfileResponse getMyInfo() {
        Integer userId = SecurityUtil.getCurrentUserId();

        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

       return UserProfileResponse.builder()
                .userId(userProfile.getId())
                .fullName(userProfile.getFullName())
                .email(userProfile.getEmail())
                .avatar(userProfile.getAvatarUrl())
                .addresses(userProfile.getAddresses())
                .phoneNumber(userProfile.getPhoneNumber())
                .dateOfBirth(userProfile.getDateOfBirth())
                .addresses(userProfile.getAddresses())
                .gender(userProfile.getGender())
                .username( SecurityUtil.getCurrentUsername())
                .role(SecurityUtil.getCurrentUserRole())
                .needsPasswordCreation (SecurityUtil.needsPasswordCreation())
               .build();
    }

    @PreAuthorize("authentication.principal.claims['role'] == 'ADMIN' or #userId == authentication.principal.claims['userId']")
    public UserProfileResponse updateUserProfile(int userId,ProfileUpdateRequest request, MultipartFile avt) {

        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        if (request != null && request.getEmail() != null
                && !request.getEmail().equals(userProfile.getEmail())) {
            if (userProfileRepository.existsUserProfileByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }

        if (request != null) {
            profileMapper.updateProfile(request, userProfile);
        }

        if (avt != null && !avt.isEmpty()) {
            String oldAvatarUrl = userProfile.getAvatarUrl();
            try {
                String url = uploadClient.uploadFile(avt, containerName);
                userProfile.setAvatarUrl(url);
                if (oldAvatarUrl != null) {
                    try {
                        uploadClient.deleteFile(oldAvatarUrl);
                    } catch (Exception e) {
                        log.warn("Failed to delete old avatar file: {}", e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }

        userProfileRepository.save(userProfile);

        Map<String, Object> data = new HashMap<>();
        data.put("fullName", userProfile.getFullName());
        data.put("updateTime", LocalDate.now());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("email")
                .recipient(userProfile.getEmail())
                .template("user_updated")
                .data(data)
                .build();
        try {
            kafkaTemplate.send("notification-delivery", notificationEvent);
        } catch (Exception e) {
            log.error("Failed to send Kafka notification event", e);
        }

        return UserProfileResponse.builder()
                .userId(userProfile.getId())
                .fullName(userProfile.getFullName())
                .avatar(userProfile.getAvatarUrl())
                .email(userProfile.getEmail())
                .phoneNumber(userProfile.getPhoneNumber())
                .dateOfBirth(userProfile.getDateOfBirth())
                .gender(userProfile.getGender())
                .addresses(userProfile.getAddresses())
                .build();
    }

    public List<UserProfileResponse> getUserProfilesInternal(String keyword) {
        Pageable pageable = PageRequest.of(0, 500);

        Specification<UserProfile> spec = (root, query, cb) -> {
            query.distinct(true);
            root.fetch("addresses", JoinType.LEFT);

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("phoneNumber")), pattern)
            );
        };

        List<UserProfile> profiles = userProfileRepository
                .findAll(spec, pageable)
                .getContent();

        return profiles.stream()
                .map(profileMapper::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("authentication.principal.claims['role'] == 'ADMIN'")
    public String deleteUserProfile(Integer userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

        try {
            authGrpcClient.deleteUser(userId);
        } catch (Exception e) {
            throw e;
        }

        userProfileRepository.delete(userProfile);

        return "User profile deleted successfully";
    }

}
