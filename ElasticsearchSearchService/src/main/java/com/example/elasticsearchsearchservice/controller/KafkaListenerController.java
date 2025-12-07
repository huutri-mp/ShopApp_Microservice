package com.example.elasticsearchsearchservice.controller;

import com.example.elasticsearchsearchservice.entity.Product;
import com.example.elasticsearchsearchservice.repository.ProductSearchRepository;
import com.example.event.dto.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaListenerController {

    private final ProductSearchRepository repository;

    @KafkaListener(topics = "product-created-topic", groupId = "search-group")
    public void handleProductCreated(ProductCreatedEvent event) {
        log.info("Received product created event: {}", event);
        Product doc = Product.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .brand(event.getBrand())
                .category(event.getCategory())
                .price(event.getPrice())
                .status(event.getStatus())
                .build();
        repository.save(doc);
    }
}
