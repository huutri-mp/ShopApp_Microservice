package com.example.paymentservce.grpc;

import com.example.paymentservce.dto.request.InitPaymentRequest;
import com.example.paymentservce.dto.response.InitPaymentResponse;
import com.example.paymentservce.dto.response.RefundPaymentResponse;
import com.example.paymentservce.service.VNPayService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import payment.Payment;
import payment.PaymentServiceGrpc;

import java.util.UUID;
@GrpcService
@RequiredArgsConstructor
@Slf4j
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final VNPayService vnPayService;

    @Override
    public void initPayment(
            Payment.InitPaymentReqest request,
            StreamObserver<Payment.InitPaymentResponse> responseObserver) {
        log.info("Received request: {}", request.getAmount());
        try {
            InitPaymentRequest initPaymentRequest = InitPaymentRequest.builder()
                    .txnRef(String.valueOf(request.getOrderId()))
                    .amount(request.getAmount())
                    .ipAddress(request.getIpAdress())
                    .requestId(UUID.randomUUID().toString())
                    .userId(request.getUserId())
                    .build();

            InitPaymentResponse vnpResponse = vnPayService.init(initPaymentRequest);

            Payment.InitPaymentResponse response =
                    Payment.InitPaymentResponse.newBuilder()
                            .setUrl(vnpResponse.getUrl())
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error init payment", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void refundPayment(
            Payment.RefundPaymentRequest request,
            StreamObserver<Payment.RefundPaymentResponse> responseObserver) {

        log.info("Received request: {}", request);

        try {
            RefundPaymentResponse response = vnPayService.refundFull(request.getOrderId());

            Payment.RefundPaymentResponse paymentResponse = Payment.RefundPaymentResponse.newBuilder()
                    .setCode(response.getCode())
                    .setMessage(response.getMessage())
                    .build();

            responseObserver.onNext(paymentResponse);
            responseObserver.onCompleted();
        }
        catch (Exception e){
            log.error("Error refund payment", e);
            responseObserver.onError(e);
        }
    }
}
