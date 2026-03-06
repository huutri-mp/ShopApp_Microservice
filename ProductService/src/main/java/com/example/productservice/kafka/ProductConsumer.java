package com.example.productservice.kafka;

import com.example.commonlib.dto.OrderEvent;
import com.example.productservice.service.ProductService;
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

    public final ProductService productService;

    @KafkaListener(
            topics = {"order-created", "order-canceled"},
            groupId = "product-service"

    )
    public void handleOrderSuccess(OrderEvent event) {
        log.info("Received product order event: {}", event);
        productService.updateStockProduct(event);
    }

}
