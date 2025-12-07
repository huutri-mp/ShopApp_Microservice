package com.example.orderservice.service;

import com.example.orderservice.dto.request.OrderRequest;
import com.example.orderservice.dto.response.InternalOrderResponse;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public interface OrderService {
    String createOrder(OrderRequest request);
    List<OrderResponse> getOrdersByUserId();
    String cancleOrder(int orderId);
    InternalOrderResponse getOrderById(int orderId);
    String updateOrderStatus(int orderId, OrderStatus status);
}
