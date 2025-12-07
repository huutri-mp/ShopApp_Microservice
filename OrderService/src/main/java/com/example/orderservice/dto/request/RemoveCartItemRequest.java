package com.example.orderservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveCartItemRequest {
    private Integer cartId;
    private List<Integer> productIds;
}

