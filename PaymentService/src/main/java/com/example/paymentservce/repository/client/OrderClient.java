package com.example.paymentservce.repository.client;

import com.example.paymentservce.enums.OrderStatus;
import com.example.paymentservce.dto.response.InternalOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient (name = "order-service", url = "${order.service.url}")
public interface OrderClient {
    @GetMapping (value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<InternalOrderResponse> getOrderById(@PathVariable("orderId") int orderId);
    @PutMapping (value = "/updateStatus/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> updateOrderStatus(@PathVariable("orderId") int orderId, @RequestBody OrderStatus orderStatus);
}
