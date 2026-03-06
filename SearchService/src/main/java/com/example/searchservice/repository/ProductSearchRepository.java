package com.example.searchservice.repository;

import com.example.searchservice.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<Product, Long> {
    boolean existsById(Long id);
    void deleteById(Long id);

}
