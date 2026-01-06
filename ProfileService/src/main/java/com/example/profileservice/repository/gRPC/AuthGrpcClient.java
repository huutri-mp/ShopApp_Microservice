package com.example.profileservice.repository.gRPC;

import auth.AuthServiceGrpc;
import auth.DeleteUserRequest;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthGrpcClient {
    @GrpcClient("auth-service")
    private AuthServiceGrpc.AuthServiceBlockingStub authServiceStub;

    public String deleteUser (int userId) {
        log.debug("gRPC DeleteUser - Request for userId: {}", userId);

        try{
            DeleteUserRequest request = DeleteUserRequest.newBuilder()
                    .setUserId(userId)
                    .build();
            String response = authServiceStub.deleteUser(request).getMessage();

            log.info("gRPC DeleteUser - Success for userId: {}, message: {}", userId, response);
            return response;
        }
        catch (Exception e){
            log.error("gRPC DeleteUser - Failed for userId: {}", userId, e);
            return "Failed to delete user";
        }
    }
}
