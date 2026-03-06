package com.example.searchservice.kafka;

import com.example.commonlib.dto.ProductEvent;
import com.example.searchservice.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaListenerController {

    private final ProductSearchService productSearchService;

    @KafkaListener(topics = "product-events")
    public void handleProductEvent(ProductEvent event) {
        log.info("Received product created event: {}", event);
         switch (event.getEventType()) {
             case CREATE,UPDATE  -> productSearchService.upsert(event);
             case DELETE -> productSearchService.delete(event.getProductId());
         }

    }
}
