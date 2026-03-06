package com.example.productservice.repository;

import com.example.productservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("""
    SELECT pv
    FROM ProductVariant pv
    JOIN FETCH pv.product p
    LEFT JOIN FETCH p.images
    WHERE pv.skuCode = :skuCode
""")
    ProductVariant findBySkuCode(String skuCode);

    List<ProductVariant> findBySkuCodeIn(List<String> skuCodes);
}
