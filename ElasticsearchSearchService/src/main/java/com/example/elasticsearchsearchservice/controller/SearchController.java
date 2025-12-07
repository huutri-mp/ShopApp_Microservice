package com.example.elasticsearchsearchservice.controller;

import com.example.elasticsearchsearchservice.constan.UrlConstant;
import com.example.elasticsearchsearchservice.entity.Product;
import com.example.elasticsearchsearchservice.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(UrlConstant.API_V1_SEARCH)
@RequiredArgsConstructor
public class SearchController {

    private final ProductSearchService productSearchService;
    @GetMapping
    public List<Product> search(@RequestParam String keyword) {
        return productSearchService.search( keyword);
    }
}