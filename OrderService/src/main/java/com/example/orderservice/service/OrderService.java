package com.example.orderservice.service;

import com.example.commonlib.dto.PagingResponse;
import com.example.orderservice.dto.request.OrderRequest;
import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.enums.OrderStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface OrderService {
    PagingResponse<OrderResponse> queryOrders(Integer page, Integer pageSize, String createdAtTo, String createdAtFrom, String orderStatus, String paymentMethod);
    OrderResponse createOrder(OrderRequest request);
    List<OrderResponse> getOrders();
    String cancleOrder(long orderId);
    String updateStatus(long orderId, OrderStatus status);
}
