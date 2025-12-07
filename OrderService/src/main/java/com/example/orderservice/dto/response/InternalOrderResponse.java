package com.example.orderservice.dto.response;


import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InternalOrderResponse {
    int orderId;
    int userId;
    double totalPrice;
    PaymentMethod paymentMethod;
    OrderStatus orderStatus;
}
