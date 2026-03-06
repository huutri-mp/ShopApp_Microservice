package com.example.authenticationservice.service.impl;

import com.example.authenticationservice.dto.request.*;
import com.example.authenticationservice.dto.response.*;
import com.example.authenticationservice.entity.AuthenUser;
import com.example.authenticationservice.entity.InvalidatedToken;
import com.example.authenticationservice.entity.RefreshToken;
import com.example.authenticationservice.service.*;
import com.example.commonlib.Enum.MailTemplate;
import com.example.commonlib.dto.PagingResponse;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.commonlib.dto.NotificationEvent;
import com.example.authenticationservice.repository.AuthRepository;
import com.example.authenticationservice.repository.InvalidatedTokenRepository;
import com.example.authenticationservice.repository.gRPCClient.*;
import com.example.authenticationservice.repository.gRPCClient.ProfileGrpcClient;
import com.example.authenticationservice.utill.JwtUtil;
import com.nimbusds.jose.JOSEException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class AuthenServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final RefreshTokenService refreshTokenService;
    private final ProfileGrpcClient profileGrpcClient;
    private final UploadGrpcClient uploadClient;
    private final PasswordEncoder passwordEncoder;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final JwtUtil jwtUtil;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FacebookIdentityService facebookIdentityService;
    private final GoogleUserService googleUserService;
    private final FacebookUserService facebookUserService;
    private final GoogleIdentityService googleIdentityService;

    @Transactional
    public String createUser(UserCreationRequest request, MultipartFile avt) {
        if(request == null) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
        if(authRepository.existsUserByUserName(request.getUserName())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if(profileGrpcClient.checkEmailExists(request.getEmail()) == true) {
            throw  new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        AuthenUser authenUser = AuthenUser.builder()
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() == null ? "USER" : request.getRole())
                .enabled(true)
                .build();

        authRepository.saveAndFlush(authenUser);

        String avtUrl = null;
        if(avt != null) {
            try {
                String url = uploadClient.uploadFile(avt, "user-image");
                avtUrl = url;
            } catch (Exception e) {
                throw e;
            }
        }

        ProfileCreationRequest profileCreationRequest = ProfileCreationRequest.builder()
                .userId(authenUser.getId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .avatarUrl(avtUrl)
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .build();

        System.out.println( "profile create"+ profileCreationRequest);
        try {
            profileGrpcClient.createProfile(profileCreationRequest);
        } catch (Exception e) {
            authRepository.delete(authenUser);
            log.error("Failed to create profile", e);
            throw new AppException(ErrorCode.PROFILE_CREATION_FAILED);
        }

        return "User created successfully";
    }

    public LoginResponse login(LoginRequest request) {
        if(request == null) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
        AuthenUser authenUser = authRepository.findByUserName(request.getUserName());
        if (authenUser == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        if(!passwordEncoder.matches(request.getPassword(), authenUser.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if(!authenUser.getEnabled()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }
        String token = jwtUtil.generateToken(authenUser);
        String refreshToken = refreshTokenService.creatRefreshToken(authenUser);
        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
        return loginResponse;
    }

    public String logout(LogoutRequest request) {
        try {
            var signToken = jwtUtil.verifyToken(request.getAccessToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = new InvalidatedToken();
            invalidatedToken.setId(jit);
            invalidatedToken.setExpiryTime(expiryTime);

            invalidatedTokenRepository.save(invalidatedToken);
            refreshTokenService.revokeToken(request.getRefreshToken());
        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.TOKEN_PARSING_ERROR);
        }
        return "Logout successful";
    }

    public LoginResponse refreshToken(String request) {
        if (request == null)
            return null;
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(request);

        if (refreshToken == null) {
            return null;
        }

        if (refreshToken.getExpiresAt().before(new Date())) {
            return null;
        }

        AuthenUser authenUser = refreshToken.getUser();
        String token = jwtUtil.generateToken(authenUser);
        return LoginResponse.builder()
                .token(token)
                .build();
    }

    public Boolean introspect(String token) {
        System.out.println("token:" + token );
        if (token == null || token.isEmpty()) {
            throw new AppException(ErrorCode.TOKEN_PARSING_ERROR);
        }

        boolean isValid = false;

        try {
            isValid = (jwtUtil.validateToken(token));
        } catch (ParseException e) {
            throw new AppException(ErrorCode.AUTH_TOKEN_INVALID);
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.AUTH_TOKEN_INVALID);
        }
        return isValid;
    }
    public LoginResponse outboundAuthenticate(String code, String provider) {
        System.out.println("code" + code);
        Map<String, String> formData = new HashMap<>();

        formData.put("code", code);
        formData.put("grant_type", "authorization_code");

        ExchangeTokenResponse response ;
        OutboundUserResponse userInfo = null;

        try {
            if ("google".equalsIgnoreCase(provider)) {
                response = googleIdentityService.exchangeToken(code);
                userInfo = googleUserService.getUserInfo(response.getAccessToken());
            } else if ("facebook".equalsIgnoreCase(provider)) {
                response = facebookIdentityService.exchangeToken(code);
                userInfo = facebookUserService.getUserInfo( response.getAccessToken());
            }
        }  catch (FeignException e) {
            String errorMessage = e.contentUTF8();
            log.error("FeignException status: {}, content: {}", e.status(), errorMessage, e);

            if (errorMessage.contains("redirect_uri_mismatch")) {
                throw new AppException(ErrorCode.REDIRECT_URI_MISMATCH);
            }

            throw new AppException(ErrorCode.OAUTH_PROVIDER_ERROR);
        }

        // Kiểm tra hoặc tạo user
        AuthenUser authenUser;
        if (profileGrpcClient.checkEmailExists(userInfo.getEmail()) == false
                && !checkUserNameExists(userInfo.getEmail())) {
            authenUser = AuthenUser.builder()
                    .userName(userInfo.getEmail())
                    .role("USER")
                    .enabled(true)
                    .build();
            authRepository.save(authenUser);
            ProfileCreationRequest profileCreationRequest = ProfileCreationRequest.builder()
                    .userId(authenUser.getId())
                    .fullName(userInfo.getName())
                    .email(userInfo.getEmail())
                    .avatarUrl(userInfo.getPicture())
                    .build();
            try {
                profileGrpcClient.createProfile(profileCreationRequest);
            } catch (Exception e) {
                authRepository.delete(authenUser);
                log.error("Failed to create profile", e);
                throw new AppException(ErrorCode.PROFILE_CREATION_FAILED);
            }

        }
        else {
            String email = userInfo.getEmail();
            UserProfileResponseInternal profileResponse = profileGrpcClient.getProfileByEmail(email);
            int userId = profileResponse.getUserId();
            authenUser = authRepository.getUserById(userId);
        }
        String jwt = jwtUtil.generateToken(authenUser);
        String refreshToken = refreshTokenService.creatRefreshToken(authenUser);
        return LoginResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    public String changePassword(ChangePasswordRequest request) {
        if (request == null || request.getNewPassword() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        AuthenUser authenUser = authRepository.findByUserName(userName);
        if (authenUser == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(request.getOldPassword(), authenUser.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        authenUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        authenUser.setLastPasswordChange(LocalDate.now());

        UserProfileResponseInternal userProfileResponseInternal = profileGrpcClient.getProfile(authenUser.getId());
        authRepository.save(authenUser);
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", userName);
        data.put(
                "changeTime",
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("email")
                .recipient(userProfileResponseInternal.getEmail())
                .template(MailTemplate.PASSWORD_CHANGED)
                .data(data)
                .build();
        try {
            kafkaTemplate.send("notification-delivery", notificationEvent);
        } catch (Exception e) {
            log.error("Failed to send Kafka notification event", e);
        }

        return "Password changed successfully";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String updateAuthUser(Integer userId, UpdateAuthUser updateAuthUser) {
        AuthenUser authenUser = authRepository.getUserById(userId);
        if (authenUser == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Optional.ofNullable(updateAuthUser.getRole())
                .ifPresent(authenUser::setRole);

        Optional.ofNullable(updateAuthUser.getEnabled())
                .ifPresent(authenUser::setEnabled);

        authRepository.save(authenUser);
        return "User updated successfully";
    }
    public String createPassword(CreatePasswordRequest request) {
        if (request == null || !StringUtils.hasText(request.getPassword())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        AuthenUser authenUser = authRepository.findByUserName(userName);
        if (authenUser == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        if (StringUtils.hasText(authenUser.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_EXISTED);
        }
        authenUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authRepository.save(authenUser);

        return "Password created successfully";
    }

    public String deleteUser(Integer userId) {
        AuthenUser authenUser = authRepository.getUserById(userId);
        if (authenUser == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        authRepository.delete(authenUser);
        return "User deleted successfully";
    }

    public Boolean checkUserNameExists (String userName) {
        return authRepository.existsUserByUserName(userName);
    }

    @Override
    public PagingResponse<AuthenUser> getUsers(
            int page,
            int size,
            Boolean enabled,
            List<Integer> userIds,
            String role,
            String sort
    ) {

        Specification<AuthenUser> spec =
                (root, query, cb) -> cb.conjunction();

        if (enabled != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("enabled"), enabled));
        }

        if (role != null && !role.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("role"), role));
        }

        if (userIds != null && !userIds.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("id").in(userIds));

        }

        Sort sortObj =
                "asc".equalsIgnoreCase(sort)
                        ? Sort.by("userName").ascending()
                        : "desc".equalsIgnoreCase(sort)
                        ? Sort.by("userName").descending()
                        : Sort.by("id").descending();

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<AuthenUser> users = authRepository.findAll(spec, pageable);
        return PagingResponse.<AuthenUser>builder()
                .items(users.getContent())
                .total(users.getTotalElements())
                .page(page)
                .size(size)
                .hasNext(users.hasNext())
                .hasPrev(users.hasPrevious())
                .build();
    }

}

