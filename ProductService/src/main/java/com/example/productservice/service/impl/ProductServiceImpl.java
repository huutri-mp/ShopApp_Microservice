package com.example.productservice.service.impl;

import com.example.commonlib.dto.PagingResponse;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.event.dto.EventType;
import com.example.event.dto.ProductEvent;
import com.example.productservice.mapper.ProductEventMapper;
import com.example.productservice.mapper.ProductMapper;
import com.example.productservice.dto.request.*;
import com.example.productservice.dto.response.*;
import com.example.productservice.entity.*;
import com.example.productservice.mapper.ProductVariantMapper;
import com.example.productservice.repository.*;
import com.example.productservice.repository.gRPC.UploadGrpcClient;
import com.example.productservice.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Predicate;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Value("${azure.storage.container-name}")
    private String containerName;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductMapper productMapper;
    private final ProductEventMapper productEventMapper;
    private final UploadGrpcClient uploadGrpcClient;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;

    private final String TOPIC = "product-events";

    @Override
    @Transactional
    @PreAuthorize("authentication.principal.claims['role'] == 'ADMIN'")
    public ProductResponse create(ProductCreationRequest req, List<MultipartFile> files) {

        Category cat = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Brand brand = brandRepository.findById(req.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        Product p = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .active(req.getActive() == null ? Boolean.TRUE : req.getActive())
                .category(cat)
                .brand(brand)
                .build();

        if (files != null) {
            log.info("Uploading {} files", files.size());
            for (MultipartFile file : files) {
                String url = uploadGrpcClient.uploadFile(file, containerName);
                p.getImages().add(ProductImage.builder().url(url).product(p).build());
            }
        }

        // Variants
        if (req.getVariants() != null) {
            p.setVariants(req.getVariants().stream()
                    .map(v -> ProductVariant.builder()
                            .sku(v.getSku())
                            .color(v.getColor())
                            .size(v.getSize())
                            .price(v.getPrice())
                            .salePrice(v.getSalePrice())
                            .stock(v.getStock())
                            .product(p)
                            .build()
                    ).toList());
        }

        Product saved = productRepository.save(p);

        publishProductEvent(EventType.CREATE, saved);

        return productMapper.toResponse(saved);
    }

    @PreAuthorize("authentication.principal.claims['role'] == 'ADMIN'")
    @Override
    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest req, List<MultipartFile> files) {

        Product p = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (req.getCategoryId() != null) {
            Category cat = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            p.setCategory(cat);
        }

        if (req.getBrandId() != null) {
            Brand b = brandRepository.findById(req.getBrandId())
                    .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
            p.setBrand(b);
        }

        if (req.getRemovedImageIds() != null) {
            p.getImages().removeIf(img ->
                    req.getRemovedImageIds().contains(img.getId())
            );
        }

        Map<Long, ProductVariant> current = p.getVariants().stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        List<ProductVariant> updated = new ArrayList<>();

        for (ProductVariantRequest v : req.getVariants()) {
            if (v.getId() != null && current.containsKey(v.getId())) {
                ProductVariant pv = current.get(v.getId());

                productVariantMapper.updateEntity(v, pv);

                updated.add(pv);
            } else {
                ProductVariant pv = productVariantMapper.toEntity(v);
                pv.setProduct(p);
                updated.add(pv);
            }
        }

        productMapper.updateProduct(p, req);
        p.getVariants().clear();
        p.getVariants().addAll(updated);


        if (files != null) {
            log.info("Uploading {} files", files.size());
            for (MultipartFile file : files) {
                String url = uploadGrpcClient.uploadFile(file, containerName);
                p.getImages().add(ProductImage.builder().url(url).product(p).build());
            }
        }

        Product saved = productRepository.save(p);

        publishProductEvent(EventType.UPDATE, saved);

        return productMapper.toResponse(saved);
    }

    @PreAuthorize("authentication.principal.claims['role'] == 'ADMIN'")
    @Override
    @Transactional
    public void delete(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(p);

        publishProductEvent(EventType.DELETE, p);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(p);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {
        Product p = productRepository.findBySlug(slug);
        if (p == null) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        return productMapper.toResponse(p);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<ProductResponse> getProducts(
            Integer page,
            Integer size,
            String keyword,
            Boolean isDesc,
            Long categoryId,
            Long brandId
    ) {
        log.info(
                "getProducts size={}, page={}, keyword={}, isDesc={}, categoryId={}, brandId={}",
                size,
                page,
                keyword,
                isDesc,
                categoryId,
                brandId
        );

        Sort sort = Boolean.TRUE.equals(isDesc)
                ? Sort.by("slug").descending()
                : Sort.by("slug").ascending();

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + keyword.toLowerCase() + "%"
                        )
                );
            }

            if (categoryId != null) {
                predicates.add(
                        cb.equal(root.get("category").get("id"), categoryId)
                );
            }

            if (brandId != null) {
                predicates.add(
                        cb.equal(root.get("brand").get("id"), brandId)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };


        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(spec, pageable);

        return PagingResponse.<ProductResponse>builder()
                .items(products.getContent().stream().map(productMapper::toResponse).toList())
                .total(products.getTotalElements())
                .page(page)
                .size(size)
                .hasNext(products.hasNext())
                .hasPrev(products.hasPrevious())
                .build();

    }

    private void publishProductEvent(EventType type, Product p) {
        try {
            ProductEvent event = productEventMapper.toEvent(type, p);
            kafkaTemplate.send(TOPIC, String.valueOf(p.getId()), event);
            log.info("Published product event: {} for {}", type, p.getId());

        } catch (Exception ex) {
            log.error("Failed publish event {}", ex.getMessage(), ex);
        }
    }
}
