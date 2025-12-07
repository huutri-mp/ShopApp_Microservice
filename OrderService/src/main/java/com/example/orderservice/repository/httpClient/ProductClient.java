package com.example.orderservice.repository.httpClient;

import com.example.orderservice.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {
    @GetMapping(value = "/checkProduct", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> checkProduct(@RequestParam("productId") int id, @RequestParam ("quantity") int quantity);
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductResponse> getInternalProductInfo(@PathVariable("productId") int id);
    @PutMapping(value = "/updateStock/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> updateProductStock(@PathVariable("productId") int id, @RequestParam("stock") int stock);
}
