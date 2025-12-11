package com.example.authenticationservice.gRPC;

import auth.*;
import com.example.authenticationservice.service.AuthService;
import com.example.authenticationservice.utill.JwtUtil;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

    private final JwtUtil jwtUtil;
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

            boolean isValid = jwtUtil.validateToken(token);
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
}