package com.example.cartservice.kafka;

import com.example.cartservice.service.CartService;
import com.example.commonlib.dto.OrderEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductConsumer {

    public final CartService cartService;

    @KafkaListener(
            topics = "order-created",
            groupId = "cart-service"

    )

    public void handleOrderSuccess(OrderEvent event) {
        log.info("Received product order event: {}", event);
        cartService.handleCreateOrder(event);
    }
}
