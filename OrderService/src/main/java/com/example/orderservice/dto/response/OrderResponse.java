package com.example.orderservice.dto.response;

import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderResponse {
    long id;
    List<OrderItemResponse> orderItems;
    BigDecimal totalAmount;
    Map<String, String> address;
    OrderStatus status;
    PaymentMethod paymentMethod;
    String paymentUrl;
    LocalDateTime createdAt;
}
