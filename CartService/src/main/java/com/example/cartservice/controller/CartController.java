package com.example.cartservice.controller;

import com.example.cartservice.constant.UrlConstant;
import com.example.cartservice.dto.requets.AddCartItemRequest;
import com.example.cartservice.dto.requets.RemoveCartItemRequest;
import com.example.cartservice.dto.response.CartResponse;
import com.example.cartservice.service.CartService;
import com.example.commonlib.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_CART_USER)
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping()
    public ApiResponse<CartResponse> getCart() {
        CartResponse response = cartService.getCarts();
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .data(response)
                .build();
    }

    @PostMapping("/addToCart")
    public ApiResponse<String> addToCart(@RequestBody AddCartItemRequest request) {
        cartService.addToCart(request);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Item added to cart successfully")
                .build();
    }

    @DeleteMapping("/removeItem")
    public ApiResponse<String> removeItem(@RequestBody RemoveCartItemRequest request) {
        log.info("Removing item from cart: {}", request);
        return ApiResponse.<String>builder()
                .code(200)
                .message(cartService.removeItem(request))
                .build();
    }

    @DeleteMapping("/clear")
    public ApiResponse<String> removeAllFromCart() {
        log.info("Removing all items from cart");
        return ApiResponse.<String>builder()
                .code(200)
                .message(cartService.clear())
                .build();
    }
}
