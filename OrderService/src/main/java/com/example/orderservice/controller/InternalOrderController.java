package com.example.orderservice.controller;

import com.example.orderservice.constan.UrlConstant;
import com.example.orderservice.dto.response.InternalOrderResponse;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.service.OrderService;
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
@RequestMapping(UrlConstant.API_V1_ORDER_INTERNAL)
public class InternalOrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/{orderId}")
    public ResponseEntity <InternalOrderResponse> getOrderById(@PathVariable int orderId) {
        log.info("Fetching order with ID: {}", orderId);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PutMapping ("/updateStatus/{orderId}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable int orderId, @RequestBody OrderStatus status) {
        log.info("Updating order status for order ID: {} to status: {}", orderId, status);
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Order status updated successfully");
    }

}
