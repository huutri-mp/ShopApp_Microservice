package com.example.orderservice.repository.gRPCClient;

import com.example.orderservice.dto.response.RefundPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import payment.Payment;
import payment.PaymentServiceGrpc;

@Service
@Slf4j
public class PaymentGrpcClient {
    @GrpcClient("payment-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceBlockingStub;

    public String initPayment (Payment.InitPaymentReqest request){
        log.info("PaymentGrpcClient initPayment, request={}", request);

        try {
            Payment.InitPaymentResponse response = paymentServiceBlockingStub.initPayment(request);

            return response.getUrl();
        }

        catch(Exception e){
            log.error("Error when calling product service", e);
            throw e;
        }
    }

    public RefundPaymentResponse refundPayment (Payment.RefundPaymentRequest request){
        log.info("PaymentGrpcClient refundPayment, request={}", request);

        try {
            Payment.RefundPaymentResponse response = paymentServiceBlockingStub.refundPayment(request);

            RefundPaymentResponse refundPaymentResponse =  RefundPaymentResponse.builder()
                    .code(response.getCode())
                    .message(response.getMessage())
                    .success(response.getSuccess())
                    .build();

            log.info("PaymentGrpcClient refundPayment, response={}, {}, {}", refundPaymentResponse.getMessage(), refundPaymentResponse.getCode(), refundPaymentResponse.getSuccess());
            return refundPaymentResponse;
        }
        catch(Exception e){
            log.error("Error when calling product service", e);
            throw e;
        }
    }
}
