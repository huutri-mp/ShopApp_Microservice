package com.example.orderservice.dto.request;

import com.example.orderservice.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    @NotNull(message = "Shipping address is required")
    Integer shippingAddress;

    @NotEmpty(message = "Order items cannot be empty")
    List<OrderItemRequest> items;

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod;

    String ipAddress;
}
