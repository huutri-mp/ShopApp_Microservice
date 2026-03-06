package com.example.paymentservce.service;

import com.example.commonlib.dto.OrderEvent;
import com.example.paymentservce.constant.*;
import com.example.paymentservce.constant.Currency;
import com.example.paymentservce.constant.Locale;
import com.example.paymentservce.dto.request.InitPaymentRequest;
import com.example.paymentservce.dto.response.InitPaymentResponse;
import com.example.paymentservce.dto.response.RefundPaymentResponse;
import com.example.paymentservce.entity.Payment;
import com.example.paymentservce.enums.PaymentMethod;
import com.example.paymentservce.repository.PaymentRepository;
import com.example.paymentservce.util.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class VNPayService implements PaymentService {

    public static final String VERSION = "2.1.0";
    public static final String COMMAND = "pay";
    public static final String ORDER_TYPE = "190000";
    public static final long DEFAULT_MULTIPLIER = 100L;

    @Value("${vnp_TmnCode}")
    private String tmnCode;

    @Value("${vnp_Url}")
    private String initPaymentPrefixUrl;

    @Value("${vnp_ReturnUrl}")
    private String returnUrlFormat;

    @Value("${vnp_timeout}")
    private Integer paymentTimeout;

    @Value("${vnp_IpnUrl}")
    private String ipnUrl;

    @Value("${vnp_RefundUrl}")
    private String refundUrl;


    private final CryptoService cryptoService;
    private final PaymentRepository paymentRepository;

    public InitPaymentResponse init(InitPaymentRequest request) {
        var amount = request.getAmount() * DEFAULT_MULTIPLIER;  // 1. amount * 100
        var txnRef = request.getTxnRef();                       // 2. bookingId
        var returnUrl = buildReturnUrl(txnRef);                 // 3. FE redirect by returnUrl
        var vnCalendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        var createdDate = DateUtil.formatVnTime(vnCalendar);
        vnCalendar.add(Calendar.MINUTE, paymentTimeout);
        var expiredDate = DateUtil.formatVnTime(vnCalendar);    // 4. expiredDate for secure

        var ipAddress = request.getIpAddress();
        var orderInfo = buildPaymentDetail(request);
        var requestId = request.getRequestId();

        Map<String, String> params = new HashMap<>();

        params.put(VNPayParams.VERSION, VERSION);
        params.put(VNPayParams.COMMAND, COMMAND);

        params.put(VNPayParams.TMN_CODE, tmnCode);
        params.put(VNPayParams.AMOUNT, String.valueOf(amount));
        params.put(VNPayParams.CURRENCY, Currency.VND.getValue());

        params.put(VNPayParams.TXN_REF, txnRef);
        params.put(VNPayParams.RETURN_URL, returnUrl);

        params.put(VNPayParams.CREATED_DATE, createdDate);
        params.put(VNPayParams.EXPIRE_DATE, expiredDate);

        params.put(VNPayParams.IP_ADDRESS, ipAddress);
        params.put(VNPayParams.LOCALE, Locale.VIETNAM.getCode());

        params.put(VNPayParams.ORDER_INFO, orderInfo);
        params.put(VNPayParams.ORDER_TYPE, ORDER_TYPE);

        var initPaymentUrl = buildInitPaymentUrl(params);
        log.debug("[request_id={}] Init payment url: {}", requestId, initPaymentUrl);
        log.info("Init payment params: {}", initPaymentUrl);
        return InitPaymentResponse.builder()
                .url(initPaymentUrl)
                .build();
    }


    public boolean verifyIpn(Map<String, String> params) {
        var reqSecureHash = params.get(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH_TYPE);
        var hashPayload = new StringBuilder();
        var fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        var itr = fieldNames.iterator();
        while (itr.hasNext()) {
            var fieldName = itr.next();
            var fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashPayload.append(fieldName);
                hashPayload.append(Symbol.EQUAL);
                hashPayload.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    hashPayload.append(Symbol.AND);
                }
            }
        }

        var secureHash = cryptoService.sign(hashPayload.toString());
        return secureHash.equals(reqSecureHash);
    }

    private String buildPaymentDetail(InitPaymentRequest request) {
        return String.format("Thanh_toan_don_hang_%s", request.getTxnRef());
    }

    private String buildReturnUrl(String txnRef) {
        return String.format(returnUrlFormat, txnRef);
    }

    @SneakyThrows
    private String buildInitPaymentUrl(Map<String, String> params) {
        StringBuilder hashPayload = new StringBuilder();
        StringBuilder query = new StringBuilder();
        List fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);   // 1. Sort field names

        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // 2.1. Build hash data
                hashPayload.append(fieldName);
                hashPayload.append(Symbol.EQUAL);
                hashPayload.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                // 2.2. Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append(Symbol.EQUAL);
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if (itr.hasNext()) {
                    query.append(Symbol.AND);
                    hashPayload.append(Symbol.AND);
                }
            }
        }
        log.info("hashPayload: {}", hashPayload);

        // 3. Build secureHash
        String secureHash = cryptoService.sign(hashPayload.toString());

        // 4. Finalize query
        query.append("&vnp_SecureHash=");
        query.append(secureHash);

        return initPaymentPrefixUrl + "?" + query;
    }

    public PaymentMethod getProvider() {
        return PaymentMethod.VNPAY;
    }

    public RefundPaymentResponse refundFull(Long orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId);

        if(payment == null) {
            throw new IllegalStateException("Payment not found");
        }

        if(payment.getRefund() == true){
            throw new IllegalStateException("Payment has been refunded");
        }

        if (payment.getPaymentMethod() != PaymentMethod.VNPAY) {
            throw new IllegalStateException("Payment method is not VNPAY");
        }

        var vnCalendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        var createdDate = DateUtil.formatVnTime(vnCalendar);
        var requestID = UUID.randomUUID().toString();

        Long amount = payment.getAmount() * DEFAULT_MULTIPLIER;

        Map<String, String> params = new LinkedHashMap<>();

        params.put(VNPayParams.REQUESTID, requestID );
        params.put(VNPayParams.VERSION, VERSION);
        params.put(VNPayParams.COMMAND, "refund");
        params.put(VNPayParams.TMN_CODE, tmnCode);
        params.put(VNPayParams.TRANSACTION_TYPE, "02");
        params.put(VNPayParams.TXN_REF, orderId.toString());
        params.put(VNPayParams.AMOUNT, String.valueOf(amount));
        params.put(VNPayParams.TRANSACTION_NO, payment.getTransactionNo());
        params.put(VNPayParams.TRANSACTION_DATE,payment.getTransactionDate());
        params.put(VNPayParams.CREATED_BY, "payment-service");
        params.put(VNPayParams.CREATED_DATE, createdDate);
        params.put(VNPayParams.IP_ADDRESS, "127.0.0.1");
        params.put(VNPayParams.ORDER_INFO, buildOrderCancleDetail( orderId.toString()));

        String dataToHash = String.join("|", params.values());

        var secureHash = cryptoService.sign(dataToHash.toString());

        params.put(VNPayParams.SECURE_HASH, secureHash);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(refundUrl, request, String.class);
        log.info("Refund response: " + response);

        String body = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> responseMap;

        try {
            responseMap = objectMapper.readValue(
                    body,
                    new TypeReference<Map<String, String>>() {}
            );
        } catch (JsonProcessingException e) {
            log.error("[VNPay Refund] Cannot parse response JSON", e);
            throw new RuntimeException("Invalid VNPay response format", e);
        }

        log.info("VNPay Refund response: {}", responseMap);

       if(responseMap.get("vnp_ResponseCode").equals("00")) {
           payment.setRefund(true);
           paymentRepository.save(payment);
           return RefundPaymentResponse.builder()
                   .code("00")
                   .message("Refund success")
                   .success(true)
                   .build();
       }
       return RefundPaymentResponse.builder()
               .code("99")
               .message("Refund failed")
               .success(false)
               .build();

    };

    private String buildOrderCancleDetail(String request) {
        return String.format("Huy_don_hang_%s", request);
    }
}

