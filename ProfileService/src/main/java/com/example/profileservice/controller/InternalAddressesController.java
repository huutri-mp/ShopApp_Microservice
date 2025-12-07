package com.example.profileservice.controller;

import com.example.profileservice.constan.UrlConstant;
import com.example.profileservice.dto.response.AddressResponse;
import com.example.profileservice.service.AddressesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(UrlConstant.API_V1_ADDRESS_INTERNAL)
public class InternalAddressesController {

    @Autowired()
    private AddressesService addressesService;

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Integer addressId) {
        log.info("Fetching address with ID: {}", addressId);
        return ResponseEntity.ok(addressesService.getAddressById(addressId));
    }
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAddress(@RequestParam Integer userId, @RequestParam Integer addressId) {
        log.info("Checking address with ID: {} for user ID: {}", addressId, userId);
        return ResponseEntity.ok(addressesService.checkAddress(userId, addressId));
    }

}
