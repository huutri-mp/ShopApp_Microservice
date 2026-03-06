package com.example.cartservice.service;

import com.example.cartservice.dto.requets.AddCartItemRequest;
import com.example.cartservice.dto.requets.RemoveCartItemRequest;
import com.example.cartservice.dto.response.CartResponse;
import com.example.commonlib.dto.OrderEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    CartResponse getCarts();
    void addToCart(AddCartItemRequest request);
    String removeItem(RemoveCartItemRequest request);
    String clear();
    void handleCreateOrder(OrderEvent event);
}
