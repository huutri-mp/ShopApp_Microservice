package com.example.paymentservce.dto.response;


import com.example.paymentservce.enums.OrderStatus;
import com.example.paymentservce.enums.PaymentMethod;
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
    int totalPrice;
    PaymentMethod paymentMethod;
    OrderStatus orderStatus;
}
