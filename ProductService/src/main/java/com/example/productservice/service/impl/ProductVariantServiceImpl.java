package com.example.productservice.service.impl;

import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.dto.request.ProductVariantRequest;
import com.example.productservice.dto.response.ProductVariantResponse;
import com.example.productservice.entity.Product;
import com.example.productservice.entity.ProductVariant;
import com.example.productservice.mapper.ProductVariantMapper;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.repository.ProductVariantRepository;
import com.example.productservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper variantMapper;

    @Override
    public ProductVariantResponse create(ProductVariantRequest request, Long productId) {
        Product product = null;
        if (productId != null) {
            product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
        }
        ProductVariant variant = ProductVariant.builder()
                .sku(request.getSku())
                .color(request.getColor())
                .size(request.getSize())
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .stock(request.getStock())
            .storage(request.getStorage())
            .ram(request.getRam())
            .cpu(request.getCpu())
            .gpu(request.getGpu())
            .screenSize(request.getScreenSize())
            .screenResolution(request.getScreenResolution())
            .batteryCapacity(request.getBatteryCapacity())
            .connectivity(request.getConnectivity())
            .warrantyMonths(request.getWarrantyMonths())
            .weight(request.getWeight())
            .dimensions(request.getDimensions())
            .material(request.getMaterial())
                .itemCondition(request.getItemCondition())
            .releaseYear(request.getReleaseYear())
                .product(product)
                .build();
        variant = variantRepository.save(variant);
        return variantMapper.toResponse(variant);
    }

    @Override
    public ProductVariantResponse getById(Long id) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        return variantMapper.toResponse(variant);
    }

    @Override
    public PagingResponse<ProductVariantResponse> getAll(int page, int size) {
        Page<ProductVariant> p = variantRepository.findAll(PageRequest.of(page, size));
        return PagingResponse.<ProductVariantResponse>builder()
                .items(p.getContent().stream().map(variantMapper::toResponse).toList())
                .total(p.getTotalElements())
                .page(page)
                .size(size)
                .hasNext(p.hasNext())
                .hasPrev(p.hasPrevious())
                .build();
    }

    @Override
    public ProductVariantResponse update(Long id, ProductVariantRequest request, Long productId) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        variant.setSku(request.getSku());
        variant.setColor(request.getColor());
        variant.setSize(request.getSize());
        variant.setPrice(request.getPrice());
        variant.setSalePrice(request.getSalePrice());
        variant.setStock(request.getStock());
        variant.setStorage(request.getStorage());
        variant.setRam(request.getRam());
        variant.setCpu(request.getCpu());
        variant.setGpu(request.getGpu());
        variant.setScreenSize(request.getScreenSize());
        variant.setScreenResolution(request.getScreenResolution());
        variant.setBatteryCapacity(request.getBatteryCapacity());
        variant.setConnectivity(request.getConnectivity());
        variant.setWarrantyMonths(request.getWarrantyMonths());
        variant.setWeight(request.getWeight());
        variant.setDimensions(request.getDimensions());
        variant.setMaterial(request.getMaterial());
        variant.setItemCondition(request.getItemCondition());
        variant.setReleaseYear(request.getReleaseYear());
        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            variant.setProduct(product);
        }
        variant = variantRepository.save(variant);
        return variantMapper.toResponse(variant);
    }

    @Override
    public void delete(Long id) {
        if (!variantRepository.existsById(id)) {
            throw new RuntimeException("Variant not found");
        }
        variantRepository.deleteById(id);
    }
}
