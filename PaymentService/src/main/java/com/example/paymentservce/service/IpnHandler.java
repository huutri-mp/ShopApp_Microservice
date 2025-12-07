package com.example.paymentservce.service;

import com.example.paymentservce.dto.response.IpnResponse;

import java.util.Map;

public interface IpnHandler {
    IpnResponse process(Map<String, String> params);
}
