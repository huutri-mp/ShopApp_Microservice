package com.example.profileservice.repository;

import com.example.profileservice.dto.response.AddressResponse;
import com.example.profileservice.entity.Addresses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AddressesRepository extends JpaRepository<Addresses, Integer> {
    List<Addresses> findByUserProfile_Id(Integer userId);
    Boolean existsAddressesByUserProfile_Id(Integer userId);
    Addresses findAddressById(Integer addressId);
}
