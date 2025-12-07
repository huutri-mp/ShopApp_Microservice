package com.example.profileservice.controller;

import com.example.commonlib.dto.ApiResponse;
import com.example.profileservice.constan.UrlConstant;
import com.example.profileservice.dto.request.AddressCreationRequest;
import com.example.profileservice.dto.request.AddressUpdateRequest;
import com.example.profileservice.entity.Addresses;
import com.example.profileservice.service.AddressesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping(UrlConstant.API_V1_ADDRESS_USER)
public class UserAddressesController {

    @Autowired
    private AddressesService addressesService;

    @PostMapping("/create")
    public ApiResponse<String> createAddress(@RequestBody AddressCreationRequest request) {
        String response = addressesService.createAddress(request);
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setData(response);
        return apiResponse;
    }

    @PutMapping("/update/{addressId}")
    public ResponseEntity<String> updateAddress(@PathVariable Integer addressId, @RequestBody AddressUpdateRequest request) {
        log.info("Updating address with ID: {}", addressId);
        String response = addressesService.updateAdderss(addressId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Integer addressId) {
        log.info("Deleting address with ID: {}", addressId);
        String response = addressesService.deleteAddress(addressId);
        return ResponseEntity.ok(response);
    }

}
