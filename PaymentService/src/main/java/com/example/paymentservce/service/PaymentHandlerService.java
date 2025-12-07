package com.example.paymentservce.service;

import com.example.paymentservce.dto.request.InitPaymentRequest;
import com.example.paymentservce.dto.response.InitPaymentResponse;
import com.example.paymentservce.dto.response.InternalOrderResponse;
import com.example.paymentservce.dto.response.PaymentResponse;
import com.example.paymentservce.enums.OrderStatus;
import com.example.paymentservce.enums.PaymentMethod;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.paymentservce.repository.client.OrderClient;
import com.example.paymentservce.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Primary
public class PaymentHandlerService {

    @Autowired
    private OrderClient orderClient;
    @Autowired VNPayService vnPayService;

    public PaymentResponse initPayment(int orderId, String ipAddress) {
        int userId = SecurityUtil.getCurrentUserId();
        InternalOrderResponse order = orderClient.getOrderById(orderId).getBody();
        if (userId != order.getUserId()) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }
        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new AppException(ErrorCode.ORDER_PAYMENT_FAILED);
        }

        String responseMessage ;
        String url = "";
        if(order.getPaymentMethod().equals(PaymentMethod.VNPAY)){
            InitPaymentRequest request = InitPaymentRequest.builder()
                    .txnRef(String.valueOf(orderId))
                    .amount(order.getTotalPrice())
                    .ipAddress(ipAddress)
                    .requestId(UUID.randomUUID().toString())
                    .userId(order.getUserId())
                    .build();
            InitPaymentResponse vnpResponse = vnPayService.init(request);
            url = vnpResponse.getUrl();
            responseMessage = "Order placed successfully with VNPay payment method. Redirecting to payment gateway...";

        }
        else if(order.getPaymentMethod().equals(PaymentMethod.CASH)){
            responseMessage = "Order placed successfully with COD payment method.";
        }
        else{
            throw new RuntimeException("Unsupported payment method: " + order.getPaymentMethod());
        }
        return PaymentResponse.builder()
                .orderId(orderId)
                .paymentMethod(order.getPaymentMethod())
                .amount(order.getTotalPrice())
                .message(responseMessage)
                .url(url)
                .build();
    }
}
