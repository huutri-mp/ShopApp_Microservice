package com.example.elasticsearchsearchservice.repository;

import com.example.elasticsearchsearchservice.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<Product, String> {
    List<Product> findByNameContainingOrDescriptionContainingOrCategoryContaining(
            String nameKeyword,
            String descriptionKeyword,
            String categoryKeyword
    );

}
