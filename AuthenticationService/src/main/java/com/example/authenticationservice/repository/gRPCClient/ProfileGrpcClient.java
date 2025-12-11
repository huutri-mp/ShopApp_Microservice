package com.example.authenticationservice.repository.gRPCClient;

import com.example.authenticationservice.dto.request.ProfileCreationRequest;
import com.example.authenticationservice.dto.response.UserProfileResponseInternal;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import profile.*;


import static com.google.common.base.Strings.nullToEmpty;

@Service
@Slf4j
public class ProfileGrpcClient {

    @GrpcClient("profile-service")
    private ProfileServiceGrpc.ProfileServiceBlockingStub profileServiceStub;

    public String createProfile(ProfileCreationRequest request) {
        log.debug("gRPC CreateProfile - Request for userId: {}, email: {}",
                request.getUserId(), request.getEmail());

        try {
            CreateProfileRequest grpcRequest = CreateProfileRequest.newBuilder()
                    .setUserId(request.getUserId())
                    .setFullName(nullToEmpty(request.getFullName()))
                    .setEmail(nullToEmpty(request.getEmail()))
                    .setPhoneNumber(nullToEmpty(request.getPhoneNumber()))
                    .setDateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth().toString() : "")
                    .setGender(nullToEmpty(request.getGender()))
                    .setAvatarUrl(nullToEmpty(request.getAvatarUrl()))
                    .build();

            CreateProfileResponse response = profileServiceStub.createProfile(grpcRequest);
            log.info("gRPC CreateProfile - Success for userId: {}, message: {}",
                    request.getUserId(), response.getMessage());
            return response.getMessage();

        } catch (StatusRuntimeException e) {
            log.error("gRPC CreateProfile - Failed for userId: {}, status: {}, description: {}",
                    request.getUserId(), e.getStatus(), e.getStatus().getDescription(), e);
            throw e;
        }
    }

    public Boolean checkEmailExists(String email) {
        log.debug("gRPC CheckEmailExists - Checking email: {}", email);

        try {
            CheckEmailRequest request = CheckEmailRequest.newBuilder()
                    .setEmail(nullToEmpty(email))
                    .build();

            CheckEmailResponse response = profileServiceStub.checkEmailExists(request);
            log.debug("gRPC CheckEmailExists - Email: {}, exists: {}", email, response.getExists());
            return response.getExists();

        } catch (StatusRuntimeException e) {
            log.error("gRPC CheckEmailExists - Failed for email: {}, status: {}",
                    email, e.getStatus(), e);
            throw e;
        }
    }

    public UserProfileResponseInternal getProfile(int userId) {
        log.debug("gRPC GetProfile - Request for userId: {}", userId);

        try {
            GetProfileRequest request = GetProfileRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            ProfileGrpcResponse response = profileServiceStub.getProfile(request);
            log.info("gRPC GetProfile - Success for userId: {}, name: {}",
                    userId, response.getFullName());
            UserProfileResponseInternal userProfileResponseInternal = UserProfileResponseInternal.builder()
                    .userId(response.getUserId())
                    .fullName(response.getFullName())
                    .email(response.getEmail())
                    .phoneNumber(response.getPhoneNumber())
                    .gender(response.getGender())
                    .build();
            return userProfileResponseInternal ;

        } catch (StatusRuntimeException e) {
            log.error("gRPC GetProfile - Failed for userId: {}, status: {}",
                    userId, e.getStatus(), e);

            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            }
            throw e;
        }
    }

    public UserProfileResponseInternal getProfileByEmail(String email) {
        log.debug("gRPC GetProfileByEmail - Request for email: {}", email);

        try {
            GetProfileByEmailRequest request = GetProfileByEmailRequest.newBuilder()
                    .setEmail(nullToEmpty(email))
                    .build();

            ProfileGrpcResponse response = profileServiceStub.getProfileByEmail(request);
            log.info("gRPC GetProfileByEmail - Success for email: {}, userId: {}",
                    email, response.getUserId());
            UserProfileResponseInternal userProfileResponseInternal = UserProfileResponseInternal.builder()
                    .userId(response.getUserId())
                    .fullName(response.getFullName())
                    .email(response.getEmail())
                    .phoneNumber(response.getPhoneNumber())
                    .gender(response.getGender())
                    .build();
            return userProfileResponseInternal;

        } catch (StatusRuntimeException e) {
            log.error("gRPC GetProfileByEmail - Failed for email: {}, status: {}",
                    email, e.getStatus(), e);

            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
            }
            throw e;
        }
    }

}

