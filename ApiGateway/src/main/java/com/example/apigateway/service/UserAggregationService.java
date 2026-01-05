package com.example.apigateway.service;

import auth.Auth;
import auth.AuthServiceGrpc;
import com.example.apigateway.dto.request.UserQueryRequest;
import com.example.apigateway.dto.response.AuthUserDto;
import com.example.apigateway.dto.response.Gender;
import com.example.apigateway.dto.response.UserProfileResponse;
import com.example.commonlib.dto.PagingResponse;
import com.google.protobuf.BoolValue;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import profile.Profile;
import profile.ProfileServiceGrpc;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserAggregationService {

    private final AuthServiceGrpc.AuthServiceBlockingStub authServiceStub;
    private final ProfileServiceGrpc.ProfileServiceBlockingStub profileServiceStub;

    public PagingResponse<UserProfileResponse> userQuery(UserQueryRequest request) {

        log.info(
                "userQuery keyword={}, role={}, enabled={}, page={}, size={}, sort={}" ,
                request.getKeyword(),
                request.getRole(),
                request.getEnabled(),
                request.getPage(),
                request.getSize(),
                request.getSort()
        );

        Profile.GetUserProfilesRequest.Builder profileRequestBuilder =
                Profile.GetUserProfilesRequest.newBuilder();

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            profileRequestBuilder.setKeyword(request.getKeyword());
        }

        Profile.GetUserProfilesRequest profileRequest =
                profileRequestBuilder.build();


        Profile.GetUserProfilesResponse
            profileResponse =  profileServiceStub.getUserProfiles(profileRequest);

        List<UserProfileResponse>  profileUsers =
                    profileResponse.getItemsList().stream()
                            .map(this::mapToUserProfileFromProfileGrpc)
                            .toList();

        if (profileUsers.isEmpty()) {
            return PagingResponse.<UserProfileResponse>builder()
                    .items(List.of())
                    .total(0)
                    .page(request.getPage())
                    .size(request.getSize())
                    .hasNext(false)
                    .hasPrev(request.getPage() > 0)
                    .build();
        }

        List<Integer> profileUserIds =
                    profileUsers.stream()
                            .map(p -> (int) p.getUserId())
                            .toList();

        Auth.GetUsersRequest.Builder authRequestBuilder =
                Auth.GetUsersRequest.newBuilder()
                        .setPage(request.getPage())
                        .setSize(request.getSize());

        authRequestBuilder.addAllUserIds(profileUserIds);

        if (request.getRole() != null) {
            authRequestBuilder.setRole(request.getRole());
        }

        if (request.getEnabled() != null) {
            authRequestBuilder.setEnabled(
                    BoolValue.of(request.getEnabled())
            );
        }

        if (request.getSort() != null) {
            authRequestBuilder.setSort(request.getSort());
        }

        Auth.GetUserResponse authResponse =
                authServiceStub.getUsers(authRequestBuilder.build());

        Map<Integer, AuthUserDto> authMap =
                authResponse.getItemsList().stream()
                        .map(u -> AuthUserDto.builder()
                                .id(u.getId())
                                .userName(u.getUserName())
                                .role(u.getRole())
                                .enabled(u.getEnabled())
                                .build())
                        .collect(Collectors.toMap(AuthUserDto::getId, u -> u));

        Map<Integer, UserProfileResponse> profileMap =
                profileUsers.stream()
                        .collect(Collectors.toMap(
                                p -> (int) p.getUserId(),
                                p -> p
                        ));

        List<UserProfileResponse> merged =
                authResponse.getItemsList().stream()
                        .map(authUser -> {
                            UserProfileResponse profile =
                                    profileMap.getOrDefault(
                                            authUser.getId(),
                                            UserProfileResponse.builder()
                                                    .userId(authUser.getId())
                                                    .build()
                                    );

                            profile.setUserName(authUser.getUserName());
                            profile.setRole(authUser.getRole());
                            profile.setEnabled(authUser.getEnabled());
                            return profile;
                        })
                        .toList();


        return PagingResponse.<UserProfileResponse>builder()
                .items(merged)
                .total(authResponse.getTotal())
                .page(authResponse.getPage())
                .size(authResponse.getSize())
                .hasNext(authResponse.getHasNext())
                .hasPrev(authResponse.getHasPrev())
                .build();
    }


    private UserProfileResponse mapToUserProfileFromProfileGrpc(
            Profile.ProfileGrpcResponse proto
    ) {
        return UserProfileResponse.builder()
                .userId(proto.getUserId())
                .fullName(proto.getFullName())
                .avatar(proto.getAvatar())
                .email(proto.getEmail())
                .phoneNumber(proto.getPhoneNumber())
                .gender(mapGender(proto.getGender()))
                .dateOfBirth(parseDate(proto.getDateOfBirth()))
                .addresses(Collections.emptyList())
                .build();
    }

    private Gender mapGender(String genderStr) {
        if (genderStr == null || genderStr.isEmpty()) return null;
        return switch (genderStr.toLowerCase(Locale.ROOT)) {
            case "male" -> Gender.MALE;
            case "female" -> Gender.FEMALE;
            case "other" -> Gender.OTHER;
            default -> null;
        };
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}
