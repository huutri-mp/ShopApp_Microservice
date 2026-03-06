package com.example.productservice.service.impl;

import com.example.commonlib.dto.OrderEvent;
import com.example.commonlib.dto.PagingResponse;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.commonlib.dto.ProductEvent;
import com.example.commonlib.dto.EventType;
import com.example.productservice.mapper.ProductEventMapper;
import com.example.productservice.mapper.ProductMapper;
import com.example.productservice.dto.request.*;
import com.example.productservice.dto.response.*;
import com.example.productservice.entity.*;
import com.example.productservice.mapper.ProductVariantMapper;
import com.example.productservice.repository.*;
import com.example.productservice.repository.gRPCClient.UploadGrpcClient;
import com.example.productservice.service.ProductService;
import com.example.productservice.util.SkuUtil;
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
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
    private final ProductMapper productMapper;
    private final ProductEventMapper productEventMapper;
    private final UploadGrpcClient uploadGrpcClient;
    private final ProductVariantMapper productVariantMapper;
    private final SkuUtil skuUtil;

    private final String TOPIC = "product-events";

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse create(ProductCreationRequest req, List<MultipartFile> files) {

        Category cat = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Brand brand = brandRepository.findById(req.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        Product p = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
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
        if (req.getVariants() != null && !req.getVariants().isEmpty()) {
            req.getVariants().forEach(v -> {
                ProductVariant pv = productVariantMapper.toEntity(v);

                pv.setSkuCode(skuUtil.generateSku(p));
                pv.setProduct(p);

                p.getVariants().add(pv);
            });
        }

        Product saved = productRepository.save(p);

        publishProductEvent(EventType.CREATE, saved);

        return productMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
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
        p.getVariants().clear();
        p.getVariants().addAll(updated);
        productMapper.updateProduct(p, req);

        if (files != null) {
            for (MultipartFile file : files) {
                String url = uploadGrpcClient.uploadFile(file, containerName);
                p.getImages().add(ProductImage.builder().url(url).product(p).build());
            }
        }

        Product saved = productRepository.save(p);

        publishProductEvent(EventType.UPDATE, saved);

        return productMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public PagingResponse<ProductResponse> getProducts(
            Integer page,
            Integer size,
            String keyword,
            Boolean isDesc,
            Long categoryId,
            Long brandId
    ) {

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
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductVariant> getProductsForOrder(List<String> skuCodes){

        List<ProductVariant> products = new ArrayList<>();
        for (String skuCode : skuCodes) {

            ProductVariant p = productVariantRepository.findBySkuCode(skuCode);
            if (p != null) {
                products.add(p);
            }
        }
        return products;
    };

    @Transactional
    public void updateStockProduct(OrderEvent event) {

        Map<String, Object> data = event.getData();
        List<Map<String, Object>> products =
                (List<Map<String, Object>>) data.get("products");

        List<String> skuCodes = products.stream()
                .map(p -> (String) p.get("skuCode"))
                .toList();

        List<ProductVariant> variants =
                productVariantRepository.findBySkuCodeIn(skuCodes);

        Map<String, ProductVariant> variantMap =
                variants.stream()
                        .collect(Collectors.toMap(
                                ProductVariant::getSkuCode,
                                v -> v
                        ));

        for (Map<String, Object> product : products) {

            String skuCode = (String) product.get("skuCode");
            Integer quantity = (Integer) product.get("quantity");

            ProductVariant pv = variantMap.get(skuCode);

            if (pv == null) {
                throw new RuntimeException("Product not found: " + skuCode);
            }

            if (pv.getStock() < quantity) {
                throw new RuntimeException("Not enough stock");
            }

            pv.setStock(pv.getStock() - quantity);
        }

        productVariantRepository.saveAll(variants);
    }

    private void publishProductEvent(EventType type, Product p) {
        try {
            ProductEvent event = productEventMapper.toEvent(type, p);
            kafkaTemplate.send(TOPIC, String.valueOf(p.getId()), event);
            log.info("Published product event: {} for {}", type, p.getId());

        } catch (Exception ex) {
            log.error("Failed to publish event {} for product {}", type, p.getId(), ex);
        }
    }
}
