package com.example.orderservice.repository.httpClient;

import com.example.orderservice.dto.response.AddressResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient (name = "address-service", url = "${address.service.url}")
public interface AddressClient {
    @GetMapping (value = "/{adderssId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AddressResponse> getAddressById(@PathVariable("adderssId") int adderssId);
    @GetMapping (value = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> checkAddress(@RequestParam("userId") int userId, @RequestParam("addressId") int addressId);
}
