package com.example.elasticsearchsearchservice.service;


import com.example.elasticsearchsearchservice.entity.Product;
import com.example.elasticsearchsearchservice.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository repository;

    public List<Product> search(String keyword) {
        return repository.findByNameContainingOrDescriptionContainingOrCategoryContaining(
                keyword, keyword, keyword
        );
    }

}