package com.example.paymentservce.repository.grpcClient;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import profile.*;

@Service
@Slf4j
public class ProfileGrpcClent {
    @GrpcClient("profile-service")
    private AddressServiceGrpc.AddressServiceBlockingStub addressServiceBlockingStub;

    @GrpcClient("profile-service")
    private ProfileServiceGrpc.ProfileServiceBlockingStub profileServiceBlockingStub;

    public AddressGrpcResponse getAddressById(int addressId){

        log.debug("ProfileGrpcClent getAddressById", addressId);
        try {
            GetAddressByIdRequest addressByIdRequest = GetAddressByIdRequest.newBuilder()
                    .setAddressId(addressId).build();

            AddressGrpcResponse addressGrpcResponse = addressServiceBlockingStub.getAddressById(addressByIdRequest);
            log.debug("ProfileGrpcClent getAddressById response: {}", addressGrpcResponse);
            return addressGrpcResponse;
        }
        catch (Exception e){
            log.error("Error when calling profile service for addressId: {}", addressId);
            throw e;
        }
    }

    public ProfileGrpcResponse getProfile (int userId) {
        log.debug("ProfileGrpcClent getProfile", userId);
        try {
            GetProfileRequest getProfileRequest = GetProfileRequest.newBuilder()
                    .setUserId(userId).build();

            ProfileGrpcResponse profileGrpcResponse = profileServiceBlockingStub.getProfile(getProfileRequest);
            log.debug("ProfileGrpcClent getProfile response: {}", profileGrpcResponse);
            return profileGrpcResponse;
        }
        catch (Exception e){
            log.error("Error when calling profile service for userId: {}", userId);
            throw e;
        }
    }
}
