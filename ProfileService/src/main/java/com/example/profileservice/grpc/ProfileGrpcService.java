package com.example.profileservice.grpc;

import com.example.commonlib.dto.PagingResponse;
import com.example.commonlib.exception.AppException;
import com.example.profileservice.dto.request.ProfileCreationRequest;
import com.example.profileservice.dto.response.UserProfileResponse;
import com.example.profileservice.dto.response.UserProfileResponseInternal;
import com.example.profileservice.enums.Gender;
import com.example.profileservice.service.UserProfileService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import profile.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.google.common.base.Strings.emptyToNull;

@GrpcService
@Slf4j
public class ProfileGrpcService extends ProfileServiceGrpc.ProfileServiceImplBase {

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public void createProfile(CreateProfileRequest request, StreamObserver<CreateProfileResponse> responseObserver) {
        log.info("gRPC CreateProfile - Received request for userId: {}, email: {}",
                request.getUserId(), request.getEmail());

        try {
            ProfileCreationRequest profileRequest = ProfileCreationRequest.builder()
                    .userId(request.getUserId())
                    .fullName(emptyToNull(request.getFullName()))
                    .email(emptyToNull(request.getEmail()))
                    .phoneNumber(emptyToNull(request.getPhoneNumber()))
                    .dateOfBirth(parseLocalDate(request.getDateOfBirth()))
                    .gender(parseGender(request.getGender()))
                    .avatarUrl(emptyToNull(request.getAvatarUrl()))
                    .build();

            String result = userProfileService.createUserProfile(profileRequest);

            CreateProfileResponse response = CreateProfileResponse.newBuilder()
                    .setMessage(result)
                    .build();

            log.info("gRPC CreateProfile - Success for userId: {}", request.getUserId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (AppException e) {
            log.error("gRPC CreateProfile - AppException for userId: {}, errorCode: {}",
                    request.getUserId(), e.getErrorCode(), e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC CreateProfile - Unexpected error for userId: {}", request.getUserId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void checkEmailExists(CheckEmailRequest request, StreamObserver<CheckEmailResponse> responseObserver) {
        log.info("gRPC CheckEmailExists - Checking email: {}", request.getEmail());

        try {
            boolean exists = userProfileService.checkEmailExists(request.getEmail());

            CheckEmailResponse response = CheckEmailResponse.newBuilder()
                    .setExists(exists)
                    .build();

            log.info("gRPC CheckEmailExists - Email: {}, exists: {}", request.getEmail(), exists);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC CheckEmailExists - Error for email: {}", request.getEmail(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void getProfile(GetProfileRequest request, StreamObserver<ProfileGrpcResponse> responseObserver) {
        log.info("gRPC GetProfile - Request for userId: {}", request.getUserId());

        try {
            UserProfileResponseInternal profile = userProfileService.getProfileById(request.getUserId());

            ProfileGrpcResponse response = buildProfileResponse(profile);

            log.info("gRPC GetProfile - Success for userId: {}, fullName: {}",
                    request.getUserId(), profile.getFullName());
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (AppException e) {
            log.error("gRPC GetProfile - AppException for userId: {}, errorCode: {}",
                    request.getUserId(), e.getErrorCode(), e);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC GetProfile - Unexpected error for userId: {}", request.getUserId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void getProfileByEmail(GetProfileByEmailRequest request, StreamObserver<ProfileGrpcResponse> responseObserver) {
        log.info("gRPC GetProfileByEmail - Request for email: {}", request.getEmail());

        try {
            UserProfileResponseInternal profile = userProfileService.getProfileByEmail(request.getEmail());

            ProfileGrpcResponse response = buildProfileResponse(profile);

            log.info("gRPC GetProfileByEmail - Success for email: {}, userId: {}",
                    request.getEmail(), profile.getUserId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (AppException e) {
            log.error("gRPC GetProfileByEmail - AppException for email: {}, errorCode: {}",
                    request.getEmail(), e.getErrorCode(), e);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC GetProfileByEmail - Unexpected error for email: {}", request.getEmail(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    private ProfileGrpcResponse buildProfileResponse(UserProfileResponseInternal profile) {
        return ProfileGrpcResponse.newBuilder()
                .setUserId(profile.getUserId())
                .setFullName(profile.getFullName() != null ? profile.getFullName() : "")
                .setEmail(profile.getEmail() != null ? profile.getEmail() : "")
                .setPhoneNumber(profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "")
                .setGender(profile.getGender() != null ? profile.getGender().name() : "")
                .setDateOfBirth(profile.getDateOfBirth() != null ? profile.getDateOfBirth().toString() : "")
                .build();
    }

    @Override
    public void getUserProfiles(
            GetUserProfilesRequest request,
            StreamObserver<GetUserProfilesResponse> responseObserver
    ) {
        log.info("gRPC GetUserProfiles - keyword: {}", request.getKeywork());

        try {
            List<UserProfileResponse> profiles =
                    userProfileService.getUserProfilesInternal(request.getKeywork());

            GetUserProfilesResponse response = GetUserProfilesResponse.newBuilder()
                    .addAllItems(
                            profiles.stream()
                                    .map(this::mapToGrpc)
                                    .toList()
                    )
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("gRPC GetUserProfiles - Unexpected error", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error")
                            .asRuntimeException()
            );
        }
    }


    private LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date: {}", dateStr);
            return null;
        }
    }

    private Gender parseGender(String genderStr) {
        if (genderStr == null || genderStr.isEmpty()) {
            return null;
        }
        try {
            return Gender.valueOf(genderStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Failed to parse gender: {}", genderStr);
            return null;
        }
    }

    private ProfileGrpcResponse mapToGrpc(UserProfileResponse dto) {
        return ProfileGrpcResponse.newBuilder()
                .setUserId(dto.getUserId())
                .setFullName(dto.getFullName() != null ? dto.getFullName() : "")
                .setEmail(dto.getEmail() != null ? dto.getEmail() : "")
                .setPhoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber() : "")
                .setGender(dto.getGender() != null ? dto.getGender().name() : "")
                .setDateOfBirth(
                        dto.getDateOfBirth() != null ? dto.getDateOfBirth().toString() : ""
                )
                .setAvatar(dto.getAvatar() != null ? dto.getAvatar() : "")
                .build();
    }


}
