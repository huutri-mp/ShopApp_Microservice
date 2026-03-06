package com.example.paymentservce.controller;


import com.example.paymentservce.constant.UrlConstant;
import com.example.paymentservce.dto.response.IpnResponse;
import com.example.paymentservce.service.IpnHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_PAYMENT_USER)
public class PaymentController {

    private final IpnHandler ipnHandler;

    @GetMapping("/vnpay_ipn")
    IpnResponse processIpn(@RequestParam Map<String, String> params) {
        log.info("[VNPay Ipn] Params: {}", params);
        return ipnHandler.process(params);
    }

}
