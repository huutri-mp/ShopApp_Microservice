package com.example.productservice.repository;

import com.example.productservice.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long>,
                                        JpaSpecificationExecutor<Brand> {

}
