package com.example.paymentservce.service;

import com.example.paymentservce.dto.request.InitPaymentRequest;
import com.example.paymentservce.dto.response.InitPaymentResponse;
import com.example.paymentservce.enums.PaymentMethod;

public interface PaymentService {
    InitPaymentResponse init(InitPaymentRequest request);
    PaymentMethod getProvider();
}
