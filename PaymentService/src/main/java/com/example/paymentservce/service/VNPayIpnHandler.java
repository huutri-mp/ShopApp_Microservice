package com.example.paymentservce.service;


import com.example.event.dto.NotificationEvent;
import com.example.paymentservce.constan.VNPayParams;
import com.example.paymentservce.constan.VnpIpnResponseConst;
import com.example.paymentservce.dto.response.InternalOrderResponse;
import com.example.paymentservce.dto.response.IpnResponse;
import com.example.paymentservce.dto.response.UserProfileResponseInternal;
import com.example.paymentservce.entity.Payment;
import com.example.paymentservce.enums.OrderStatus;
import com.example.paymentservce.enums.PaymentMethod;
import com.example.commonlib.exception.AppException;
import com.example.paymentservce.repository.PaymentRepository;
import com.example.paymentservce.repository.client.OrderClient;
import com.example.paymentservce.repository.client.ProfileClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class VNPayIpnHandler implements IpnHandler {

    private final OrderClient orderClient;
    private final VNPayService vnPayService;
    private final PaymentRepository paymentRepository;
    private final ProfileClient profileClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public IpnResponse process(Map<String, String> params) {
        if (!vnPayService.verifyIpn(params)) {
            log.warn("[VNPay IPN] Invalid signature for params: {}", params);
            return VnpIpnResponseConst.SIGNATURE_FAILED;
        }

        String txnRef = params.get(VNPayParams.TXN_REF);
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        long amount = Long.parseLong(params.get(VNPayParams.AMOUNT)) / 100;

        log.info("[VNPay IPN] Processing txnRef: {}, amount: {}, responseCode: {}, status: {}",
                txnRef, amount, responseCode, transactionStatus);

        try {
            long orderId = Long.parseLong(txnRef);

            if (isSuccessPayment(responseCode, transactionStatus)) {
                orderClient.updateOrderStatus((int)orderId, OrderStatus.PAID);

                Payment payment = Payment.builder()
                        .orderId((int) orderId)
                        .amount(amount)
                        .paymentMethod(PaymentMethod.VNPAY)
                        .build();

                paymentRepository.save(payment);
                log.info("[VNPay IPN] Successfully processed payment for order: {}", orderId);

                // Gui qua kafka notification
                InternalOrderResponse order = orderClient.getOrderById((int)orderId).getBody();
                UserProfileResponseInternal userProfileResponseInternal = profileClient.getProfile(order.getUserId()).getBody();
                Map<String, Object> data = new HashMap<>();
                data.put("fullName", userProfileResponseInternal.getFullName());
                data.put("orderId", order.getOrderId());
                data.put("paymentTime", LocalDate.now());

                NotificationEvent notificationEvent = NotificationEvent.builder()
                        .channel("email")
                        .recipient(userProfileResponseInternal.getEmail())
                        .template("payment_success")
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
                    orderClient.updateOrderStatus((int)orderId, OrderStatus.CANCELED);
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
