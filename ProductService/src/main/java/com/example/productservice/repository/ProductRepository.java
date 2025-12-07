package com.example.productservice.repository;

import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {

    List<Product> findByPriceBetween (Double minPrice, Double maxPrice);
    Boolean existsByName(String name);
}
