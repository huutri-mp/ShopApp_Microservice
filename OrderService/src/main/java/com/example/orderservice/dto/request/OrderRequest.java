package com.example.orderservice.dto.request;

import com.example.orderservice.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
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
    @NotNull(message = "User ID cannot be blank")
    Integer userId;
    @NotNull (message = "Description cannot be blank")
    Integer shippingAddress;
    @NotBlank (message = "Description cannot be blank")
    List<Integer> selectedItems;
    @NotBlank (message = "Payment method cannot be blank")
    PaymentMethod paymentMethod;

}
