package com.example.productservice.repository;

import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
                                            JpaSpecificationExecutor<Product> {
    List<Product> findByCategory_Id(Long categoryId);
    Product findBySlug(String slug);
}

