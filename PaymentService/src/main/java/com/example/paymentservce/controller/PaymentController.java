package com.example.paymentservce.controller;


import com.example.paymentservce.constan.UrlConstant;
import com.example.paymentservce.dto.response.PaymentResponse;
import com.example.paymentservce.service.PaymentHandlerService;
import com.example.paymentservce.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_PAYMENT_USER)
public class PaymentController {
    @Autowired
    private PaymentHandlerService paymentHandlerService;

    @GetMapping ("/initPayment/{orderId}")
    public ResponseEntity<PaymentResponse> initPayment(@PathVariable int  orderId, HttpServletRequest request) {
        log.info("Initializing payment for order ID: {}", orderId);
        String ipAddress = RequestUtil.getIpAddress(request);
        return ResponseEntity.ok(paymentHandlerService.initPayment(orderId, ipAddress));
    }

}
