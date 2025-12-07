package com.example.cartservice.repository.httpClient;

import com.example.cartservice.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") int id);

    @GetMapping(value = "/checkExists/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> checkProductAvailability(@PathVariable("productId") int id);

    @PutMapping(value = "/updateStock/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> updateProductStock(@PathVariable("productId") int id, @RequestParam("stock") int stock);
}
