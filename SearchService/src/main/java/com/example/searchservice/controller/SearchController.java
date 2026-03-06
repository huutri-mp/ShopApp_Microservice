package com.example.searchservice.controller;

import com.example.commonlib.dto.PagingResponse;
import com.example.searchservice.Enum.Sort;
import com.example.searchservice.constant.UrlConstant;
import com.example.searchservice.entity.Product;
import com.example.searchservice.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(UrlConstant.API_V1_SEARCH)
@RequiredArgsConstructor
public class SearchController {

    private final ProductSearchService productSearchService;

    @GetMapping
    public PagingResponse<Product> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) Sort sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        return productSearchService.search(
                keyword,
                categoryIds,
                brandIds,
                isFeatured,
                minPrice,
                maxPrice,
                sort,
                page,
                size
        );
    }

}
