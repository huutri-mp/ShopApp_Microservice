package com.example.searchservice.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import com.example.commonlib.dto.PagingResponse;
import com.example.commonlib.dto.ProductEvent;
import com.example.searchservice.Enum.Sort;
import com.example.searchservice.entity.Product;
import com.example.searchservice.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.domain.PageRequest;



import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;

    public PagingResponse<Product> search(
            String keyword,
            List<Long> categoryIds,
            List<Long> brandIds,
            Boolean isFeatured,
            Double minPrice,
            Double maxPrice,
            Sort sort,
            int page,
            int size
    ) {
        var queryBuilder = NativeQuery.builder();

        Query keywordQuery = (keyword != null && !keyword.isBlank()) ? Query.of(q -> q
                .multiMatch(m -> m
                        .query(keyword)
                        .fields("name", "categoryName", "brandName")
                        .operator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.And)
                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                )
        ) : null;

        final List<Query> filters = new ArrayList<>();
        if (sort != null) {
            switch (sort) {
                case ASC -> queryBuilder.withSort(s -> s
                        .field(f -> f
                                .field("minPrice")
                                .order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)
                        )
                );

                case DESC -> queryBuilder.withSort(s -> s
                        .field(f -> f
                                .field("minPrice")
                                .order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)
                        )
                );

            }
        }

        if (isFeatured != null) {
            filters.add(Query.of(q -> q
                    .term(t -> t
                            .field("isFeatured")
                            .value(FieldValue.of(isFeatured))
                    )
            ));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            filters.add(Query.of(q -> q
                    .terms(t -> t
                            .field("categoryId")
                            .terms(v -> v.value(
                                    categoryIds.stream()
                                            .map(FieldValue::of)
                                            .toList()
                            ))
                    )
            ));

        }

        if (brandIds != null && !brandIds.isEmpty()) {
            filters.add(Query.of(q -> q
                    .terms(t -> t
                            .field("brandId")
                            .terms(v -> v.value(
                                    brandIds.stream()
                                            .map(FieldValue::of)
                                            .toList()
                            ))
                    )
            ));
        }

        if (minPrice != null || maxPrice != null) {
            filters.add(Query.of(q -> q
                    .range(r -> r
                            .number(n -> {
                                n.field("minPrice");
                                if (minPrice != null) n.gte(minPrice);
                                if (maxPrice != null) n.lte(maxPrice);
                                return n;
                            })
                    )
            ));
        }

        Query finalQuery;

        if (keywordQuery != null && !filters.isEmpty()) {
            finalQuery = Query.of(q -> q.bool(b -> b
                    .must(keywordQuery)
                    .filter(filters)
            ));
        } else if (!filters.isEmpty()) {
            finalQuery = Query.of(q -> q.bool(b -> b.filter(filters)));
        } else if (keywordQuery != null) {
            finalQuery = keywordQuery;
        } else {
            finalQuery = Query.of(q -> q.matchAll(m -> m));
        }

        queryBuilder
                .withQuery(finalQuery)
                .withPageable(PageRequest.of(page, size));

        var searchHits = elasticsearchOperations
                .search(queryBuilder.build(), Product.class);

        List<Product> items = searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();

        long total = searchHits.getTotalHits();

        return PagingResponse.<Product>builder()
                .items(items)
                .total(total)
                .page(page)
                .size(size)
                .hasNext((long) (page + 1) * size < total)
                .hasPrev(page > 0)
                .build();
    }


    public void upsert(ProductEvent event) {
        Product product = mapToProduct(event);
        repository.save(product);
        log.info("Upserted product {} in Elasticsearch", event.getProductId());
    }

    public void delete(Long productId) {
        if (!repository.existsById(productId)) {
            log.warn("Product {} not found in Elasticsearch", productId);
            return;
        }

        repository.deleteById(productId);

        log.info("Deleted product {} from Elasticsearch", productId);
    }

    private Product mapToProduct(ProductEvent event) {
        return Product.builder()
                .id(event.getProductId())
                .isFeatured(event.getIsFeatured())
                .name(event.getName())
                .categoryId(event.getCategoryId())
                .brandId(event.getBrandId())
                .image(event.getImage())
                .categoryName(event.getCategoryName())
                .brandName(event.getBrandName())
                .minPrice(event.getMinPrice())
                .maxPrice(event.getMaxPrice())
                .build();
    }

}