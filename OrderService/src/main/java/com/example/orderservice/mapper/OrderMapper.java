package com.example.orderservice.mapper;

import com.example.orderservice.dto.response.OrderResponse;
import com.example.orderservice.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(source = "items", target = "orderItems")
    OrderResponse toResponse(Order order);
}

