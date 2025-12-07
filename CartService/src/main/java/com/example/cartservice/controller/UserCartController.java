package com.example.cartservice.controller;

import com.example.cartservice.constan.UrlConstant;
import com.example.cartservice.dto.requets.AddCartItemRequest;
import com.example.cartservice.dto.requets.RemoveCartItemRequest;
import com.example.cartservice.dto.response.CartResponse;
import com.example.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_CART_USER)
public class UserCartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/getCart/{cartId}")
    public ResponseEntity<List<CartResponse>> getCart(@PathVariable("cartId") int cartId) {
        log.info("Fetching cart with ID: {}", cartId);
        return ResponseEntity.ok(cartService.getCart(cartId));
    }

    @PostMapping("/addToCart")
    public ResponseEntity<String> addToCart(@RequestBody AddCartItemRequest request) {
        log.info("Adding item to cart: {}", request);
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @DeleteMapping("/removeFromCart")
    public ResponseEntity<String> removeFromCart(@RequestBody RemoveCartItemRequest request) {
        log.info("Removing item from cart: {}", request);
        return ResponseEntity.ok(cartService.removeFromCart(request));
    }

    @DeleteMapping("/removeAll/{id}")
    public ResponseEntity<String> removeAllFromCart(@PathVariable("id") int id) {
        log.info("Removing all items from cart with ID: {}", id);
        return ResponseEntity.ok(cartService.removeAllFromCart(id));
    }
}
