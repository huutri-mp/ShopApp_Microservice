package com.example.profileservice.grpc;

import com.example.commonlib.exception.AppException;
import com.example.profileservice.dto.response.AddressResponse;
import com.example.profileservice.service.AddressesService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import profile.*;

@GrpcService
@Slf4j
public class AddressGrpcService extends AddressServiceGrpc.AddressServiceImplBase {

    @Autowired
    private AddressesService addressesService;

    @Override
    public void getAddressById(GetAddressByIdRequest request, StreamObserver<AddressGrpcResponse> responseObserver) {
        log.info("gRPC GetAddressById - Request for addressId: {}", request.getAddressId());

        try {
            AddressResponse address = addressesService.getAddressById(request.getAddressId());

            AddressGrpcResponse response = AddressGrpcResponse.newBuilder()
                    .setAddressId(request.getAddressId())
                    .setContactName(address.getContactName() != null ? address.getContactName() : "")
                    .setContactPhone(address.getContactPhone() != null ? address.getContactPhone() : "")
                    .setAddressLine(address.getAddressLine() != null ? address.getAddressLine() : "")
                    .setWards(address.getWards() != null ? address.getWards() : "")
                    .setProvince(address.getProvince() != null ? address.getProvince() : "")
                    .setCountry(address.getCountry() != null ? address.getCountry() : "")
                    .setIsDefault(address.getIsDefault() != null ? address.getIsDefault() : false)
                    .build();

            log.info("gRPC GetAddressById - Success for addressId: {}, contactName: {}",
                    request.getAddressId(), address.getContactName());
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (AppException e) {
            log.error("gRPC GetAddressById - AppException for addressId: {}, errorCode: {}",
                    request.getAddressId(), e.getErrorCode(), e);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC GetAddressById - Unexpected error for addressId: {}", request.getAddressId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }

    @Override
    public void checkAddress(CheckAddressRequest request, StreamObserver<CheckAddressResponse> responseObserver) {
        log.info("gRPC CheckAddress - Request for userId: {}, addressId: {}",
                request.getUserId(), request.getAddressId());

        try {
            Boolean isValid = addressesService.checkAddress(request.getUserId(), request.getAddressId());

            CheckAddressResponse response = CheckAddressResponse.newBuilder()
                    .setValid(isValid != null ? isValid : false)
                    .build();

            log.info("gRPC CheckAddress - Success for userId: {}, addressId: {}, valid: {}",
                    request.getUserId(), request.getAddressId(), isValid);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (AppException e) {
            log.error("gRPC CheckAddress - AppException for userId: {}, addressId: {}, errorCode: {}",
                    request.getUserId(), request.getAddressId(), e.getErrorCode(), e);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC CheckAddress - Unexpected error for userId: {}, addressId: {}",
                    request.getUserId(), request.getAddressId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error")
                    .asRuntimeException());
        }
    }
}
