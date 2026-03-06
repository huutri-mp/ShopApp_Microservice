package com.example.paymentservce.service;


import com.example.commonlib.Enum.MailTemplate;
import com.example.commonlib.dto.NotificationEvent;
import com.example.paymentservce.constant.VNPayParams;
import com.example.paymentservce.constant.VnpIpnResponseConst;
import com.example.paymentservce.dto.response.IpnResponse;
import com.example.paymentservce.entity.Payment;
import com.example.paymentservce.enums.OrderStatus;
import com.example.paymentservce.enums.PaymentMethod;
import com.example.commonlib.exception.AppException;
import com.example.paymentservce.repository.PaymentRepository;

import com.example.paymentservce.repository.grpcClient.OrderGrpcClient;
import com.example.paymentservce.repository.grpcClient.ProfileGrpcClent;
import com.example.paymentservce.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import profile.ProfileGrpcResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class VNPayIpnHandler implements IpnHandler {

    private final VNPayService vnPayService;
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProfileGrpcClent profileClient;
    private final OrderGrpcClient orderClient;

    public IpnResponse process(Map<String, String> params) {

        Integer userId = SecurityUtil.getCurrentUserId();
        if (!vnPayService.verifyIpn(params)) {
            log.warn("[VNPay IPN] Invalid signature for params: {}", params);
            return VnpIpnResponseConst.SIGNATURE_FAILED;
        }

        String txnRef = params.get(VNPayParams.TXN_REF);
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String transactionDate = params.get("vnp_PayDate");
        String transactionNo = params.get("vnp_TransactionNo");

        Long amount = Long.parseLong(params.get(VNPayParams.AMOUNT))/100L;

        log.info("[VNPay IPN] Processing txnRef: {}, amount: {}, responseCode: {}, status: {}",
                txnRef, amount, responseCode, transactionStatus);

        try {
            long orderId = Long.parseLong(txnRef);

            if (isSuccessPayment(responseCode, transactionStatus)) {
                orderClient.updateStatus(orderId, OrderStatus.PAID);

                Payment payment = Payment.builder()
                        .orderId((int) orderId)
                        .amount(amount)
                        .transactionDate(transactionDate)
                        .paymentMethod(PaymentMethod.VNPAY)
                        .transactionNo(transactionNo)
                        .build();

                paymentRepository.save(payment);
                log.info("[VNPay IPN] Successfully processed payment for order: {}", orderId);
                ProfileGrpcResponse userProdile = profileClient.getProfile(userId);

                // kafka notification
                Map<String, Object> data = new HashMap<>();
                data.put("fullName", userProdile.getFullName());
                data.put("orderId", orderId);
                data.put("paymentTime", LocalDate.now());

                NotificationEvent notificationEvent = NotificationEvent.builder()
                        .channel("email")
                        .recipient(userProdile.getEmail())
                        .template(MailTemplate.PAYMENT_SUCCESS)
                        .data(data)
                        .build();
                try {
                    kafkaTemplate.send("notification-delivery", notificationEvent);
                } catch (Exception e) {
                    log.error("Failed to send Kafka notification event", e);
                }

                return VnpIpnResponseConst.SUCCESS;
            } else {
                log.warn("[VNPay IPN] Payment failed. ResponseCode: {}, Status: {}", responseCode, transactionStatus);
                try {
                    orderClient.updateStatus(orderId, OrderStatus.CANCELED);
                } catch (Exception ex) {
                    log.error("[VNPay IPN] Failed to update order to CANCELED", ex);
                }
                return new IpnResponse("99", "Giao dịch không thành công");
            }

        } catch (NumberFormatException e) {
            log.error("[VNPay IPN] Invalid txnRef format: {}", txnRef, e);
            return VnpIpnResponseConst.ORDER_NOT_FOUND;
        } catch (AppException e) {
            log.error("[VNPay IPN] App exception: {}", e.getErrorCode(), e);
            return switch (e.getErrorCode()) {
                case ORDER_NOT_FOUND -> VnpIpnResponseConst.ORDER_NOT_FOUND;
                default -> VnpIpnResponseConst.UNKNOWN_ERROR;
            };
        } catch (Exception e) {
            log.error("[VNPay IPN] Unexpected error", e);
            return VnpIpnResponseConst.UNKNOWN_ERROR;
        }
    }


    private boolean isSuccessPayment(String responseCode, String transactionStatus) {
        return "00".equals(responseCode) && "00".equals(transactionStatus);
    }
}
