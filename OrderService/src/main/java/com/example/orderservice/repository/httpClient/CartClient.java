package com.example.orderservice.repository.httpClient;

import com.example.orderservice.dto.request.RemoveCartItemRequest;
import com.example.orderservice.dto.response.InternalCartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "cart-service", url = "${cart.service.url}")
public interface CartClient {
    @GetMapping (value = "/{UserId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity <InternalCartResponse> getInternalCart (@PathVariable ("UserId") Integer userId);
    @PostMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> removeSelectedItemsFromCart(@RequestBody RemoveCartItemRequest request);


}
