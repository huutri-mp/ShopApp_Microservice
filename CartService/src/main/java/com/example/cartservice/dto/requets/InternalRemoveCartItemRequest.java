package com.example.cartservice.dto.requets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalRemoveCartItemRequest {
    private Integer cartId;
    private List<Integer> productIds;
}
