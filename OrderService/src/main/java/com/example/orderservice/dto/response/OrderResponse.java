package com.example.orderservice.dto.response;

import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    int id;
    List<OrderItemResponse> orderItems;
    double totalAmount;
    AddressResponse shippingAddress;
    OrderStatus status;
    PaymentMethod paymentMethod;
}
