package com.example.profileservice.service.Impl;

import com.example.profileservice.dto.request.AddressCreationRequest;
import com.example.profileservice.dto.request.AddressUpdateRequest;
import com.example.profileservice.dto.response.AddressResponse;
import com.example.profileservice.entity.Addresses;
import com.example.profileservice.entity.UserProfile;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.profileservice.mapper.AddressMapper;
import com.example.profileservice.repository.AddressesRepository;
import com.example.profileservice.repository.UserProfileRepository;
import com.example.profileservice.service.AddressesService;
import com.example.profileservice.util.SecurityUtil;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Primary
@Slf4j
public class AddressesServiceImpl implements AddressesService {
    @Autowired
    private AddressesRepository addressesRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private AddressMapper addressMapper;

    public String createAddress(AddressCreationRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.INVALID_ADDRESS_DATA);
        }
        Integer userId = SecurityUtil.getCurrentUserId();
        UserProfile userProfile = userProfileRepository.findById(userId).orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
        List<Addresses> addresses = addressesRepository.findByUserProfile_Id(userId);
        boolean isDuplicate = addresses.stream().anyMatch(addr ->
                addr.getContactName().equals(request.getContactName()) &&
                        addr.getContactPhone().equals(request.getContactPhone()) &&
                        addr.getAddressLine().equals(request.getAddressLine()) &&
                        addr.getWards().equals(request.getWards()) &&
                        addr.getProvince().equals(request.getProvince())
        );

        if (isDuplicate) {
            throw new AppException(ErrorCode.ADDRESS_ALREADY_EXISTS);
        }

        Addresses newAddress = addressMapper.createAddress(request);
        addressesRepository.save(newAddress);
        return "Address created successfully";
    }

    public String updateAdderss(Integer addressId, AddressUpdateRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.INVALID_ADDRESS_DATA);
        }

        Addresses address = addressesRepository.findById(addressId).orElseThrow(
                () -> new AppException(ErrorCode.ADDRESS_NOT_FOUND)
        );

        UserProfile userProfile = address.getUserProfile();
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            List<Addresses> addresses = addressesRepository.findByUserProfile_Id(userProfile.getId());
            for (Addresses addr : addresses) {
                if (Boolean.TRUE.equals(addr.getIsDefault())) {
                    addr.setIsDefault(false);
                }
            }
            addressesRepository.saveAll(addresses);
            address.setIsDefault(true);
        }

        addressMapper.updateAddress(request, address);
        addressesRepository.save(address);

        return "Address updated successfully";
    }


    public AddressResponse getAddressById(Integer addressId) {
        if (addressId == null) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        Addresses address = addressesRepository.findAddressById(addressId);
        if (address == null) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }
        log.info("Retrieved address with id {}", addressId);
        return AddressResponse.builder()
                .contactName(address.getContactName())
                .contactPhone(address.getContactPhone())
                .addressLine(address.getAddressLine())
                .wards(address.getWards())
                .province(address.getProvince())
                .isDefault(address.getIsDefault() != null ? address.getIsDefault() : false)
                .build();

    }

    public Boolean checkAddress(Integer userId, Integer addressId) {
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (addressId == null) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }

        Addresses address = addressesRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        return Integer.valueOf(address.getUserProfile().getId()).equals(userId);

    }


    public String deleteAddress(Integer addressId) {
        Addresses address = addressesRepository.findById(addressId).orElseThrow(
                () -> new AppException(ErrorCode.ADDRESS_NOT_FOUND)
        );
        addressesRepository.delete(address);
        return "Address deleted successfully";
    }

}
