package com.example.authenticationservice.gRPC;

import auth.*;
import com.example.authenticationservice.entity.AuthenUser;
import com.example.authenticationservice.service.AuthService;
import com.example.authenticationservice.utill.JwtUtil;
import com.example.commonlib.dto.PagingResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthService authService;

    @Override
    public void introspect(IntrospectRequest request,
                           StreamObserver<auth.IntrospectResponse> responseObserver) {
        try {
            String token = request.getToken();
            log.info("gRPC Introspect - Validating token: {}", token);
            
            if (token == null || token.isEmpty()) {
                auth.IntrospectResponse response = auth.IntrospectResponse.newBuilder()
                        .setValid(false)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }

            boolean isValid = authService.introspect(token);
            log.info("gRPC Introspect - Token valid: {}", isValid);

            auth.IntrospectResponse response = auth.IntrospectResponse.newBuilder()
                    .setValid(isValid)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC Introspect - Error validating token", e);
            auth.IntrospectResponse response = auth.IntrospectResponse.newBuilder()
                    .setValid(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteUser(DeleteUserRequest request,
                           StreamObserver<DeleteUserResponse> responseObserver) {
        try {
            String message = authService.deleteUser(request.getUserId());
            log.info("gRPC DeleteUser - User {} deleted successfully", request.getUserId());

            DeleteUserResponse response = DeleteUserResponse.newBuilder()
                    .setMessage(message)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC DeleteUser - Failed to delete user {}", request.getUserId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUsers(GetUsersRequest request,
                         StreamObserver<GetUserResponse> responseObserver) {
        try {
            int page = request.getPage();
            int size = request.getSize() > 0 ? request.getSize() : 10;
            Boolean enabled = request.hasEnabled()
                    ? request.getEnabled().getValue()
                    : null;
            List<Integer> userIds = request.getUserIdsList();
            String role = request.getRole();
            String sort = request.getSort();
            PagingResponse<AuthenUser> pagingResponse = authService.getUsers(page, size, enabled, userIds, role, sort);

            GetUserResponse.Builder responseBuilder = GetUserResponse.newBuilder()
                    .setTotal(pagingResponse.getTotal())
                    .setPage(pagingResponse.getPage())
                    .setSize(pagingResponse.getSize())
                    .setHasNext(pagingResponse.isHasNext())
                    .setHasPrev(pagingResponse.isHasPrev());
            
            for (AuthenUser user : pagingResponse.getItems()) {
                UserGrpcResponse userGrpc = UserGrpcResponse.newBuilder()
                        .setId(user.getId())
                        .setUserName(user.getUserName() != null ? user.getUserName() : "")
                        .setRole(user.getRole() != null ? user.getRole() : "")
                        .setEnabled(user.getEnabled() != null ? user.getEnabled() : false)
                        .build();
                responseBuilder.addItems(userGrpc);
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
            log.info("gRPC GetUsers - Returned {} users", pagingResponse.getItems().size());
        } catch (Exception e) {
            log.error("gRPC GetUsers - Failed to fetch users", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }
}