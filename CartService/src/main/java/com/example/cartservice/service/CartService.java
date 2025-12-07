package com.example.cartservice.service;

import com.example.cartservice.dto.requets.AddCartItemRequest;
import com.example.cartservice.dto.requets.InternalRemoveCartItemRequest;
import com.example.cartservice.dto.requets.RemoveCartItemRequest;
import com.example.cartservice.dto.response.CartResponse;
import com.example.cartservice.dto.response.InternalCartResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {
    List<CartResponse> getCart(int cartId);
    String addToCart(AddCartItemRequest request);
    String removeFromCart(RemoveCartItemRequest request);
    String removeAllFromCart(Integer cartId);
    InternalCartResponse getInternalCart(Integer userId);
    String removeSelectedItemsFromCart ( InternalRemoveCartItemRequest request);
}
