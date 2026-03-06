package com.example.orderservice.controller;

import com.example.commonlib.dto.ApiResponse;
import com.example.commonlib.dto.PagingResponse;
import com.example.orderservice.constant.UrlConstant;
import com.example.orderservice.dto.request.OrderRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.enums.PaymentMethod;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(UrlConstant.API_V1_ORDER)
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request, HttpServletRequest httpServletRequest){
        String ipAddress = RequestUtil.getIpAddress(httpServletRequest);
        request.setIpAddress(ipAddress);
        OrderResponse response = orderService.createOrder(request);

        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .data(response)
                .build();

    }

    @GetMapping()
    public ApiResponse<List<OrderResponse>> viewOrders() {
        List<OrderResponse> responses = orderService.getOrders();
        return ApiResponse.<List<OrderResponse>>builder()
                .code(200)
                .data(responses)
                .build();
    }

    @GetMapping("/admin")
    public PagingResponse<OrderResponse> queryOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String createdAtTo,
            @RequestParam(required = false) String createdAtFrom,
            @RequestParam(required = false) String orderStatus ,
            @RequestParam(required = false) String paymentMethod
            ) {

        PagingResponse<OrderResponse> response = orderService.queryOrders(
                page,
                size,
                createdAtTo,
                createdAtFrom,
                orderStatus,
                paymentMethod
        );

        return response;

    }


    @DeleteMapping("/{orderId}")
    public ApiResponse<String> cancelOrder(@PathVariable int orderId) {
        String response = orderService.updateStatus(orderId, OrderStatus.CANCELED);
        return ApiResponse.<String>builder()
                .code(200)
                .message(response)
                .build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}")
    public ApiResponse<String> updateStatus(@PathVariable int orderId, @RequestParam String orderStatus){
        String response = orderService.updateStatus(orderId, OrderStatus.valueOf(orderStatus.trim()));
        return ApiResponse.<String>builder()
                .code(200)
                .message(response)
                .build();
    }
}
