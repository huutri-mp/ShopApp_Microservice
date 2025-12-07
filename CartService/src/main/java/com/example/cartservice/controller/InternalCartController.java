package com.example.cartservice.controller;

import com.example.cartservice.constan.UrlConstant;
import com.example.cartservice.dto.requets.InternalRemoveCartItemRequest;
import com.example.cartservice.dto.requets.RemoveCartItemRequest;
import com.example.cartservice.dto.response.InternalCartResponse;
import com.example.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_CART_INTERNAL)
public class InternalCartController {
    @Autowired
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<InternalCartResponse> getInternalCart(@PathVariable Integer userId) {
        log.info("Fetching internal cart for userId: {}", userId);
        InternalCartResponse response = cartService.getInternalCart(userId);
        if (response == null) {
            log.warn("No internal cart found for userId: {}", userId);
            return ResponseEntity.notFound().build();
        }
        log.info("Internal cart fetched successfully for userId: {}", userId);
        return ResponseEntity.ok(response);

    }
    @PostMapping("/remove")
    public String removeSelectedItemsFromCart(@RequestBody InternalRemoveCartItemRequest request) {
        log.info("Removing selected items from cart: {}", request);
        String response = cartService.removeSelectedItemsFromCart(request);
        log.info("Items removed successfully from cart: {}", request);
        return response;
    }
}
