package com.example.orderservice.controller;

import com.example.orderservice.constan.UrlConstant;
import com.example.orderservice.dto.request.OrderRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.SecurityUtil;
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
    @RequestMapping(UrlConstant.API_V1_ORDER_USER)
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request){
        log.info("Creating order for user: {}", request.getUserId());
        String response = orderService.createOrder(request);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/viewByUser")
    public ResponseEntity<List<OrderResponse>> viewOrdersByUser() {
        int userId = SecurityUtil.getCurrentUserId();
        log.info("Retrieving orders for user: {}", userId );
        return ResponseEntity.ok(orderService.getOrdersByUserId());
    }

    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable int orderId) {
        log.info("Cancelling order with ID: {}", orderId);
        orderService.cancleOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully");
    }


}
