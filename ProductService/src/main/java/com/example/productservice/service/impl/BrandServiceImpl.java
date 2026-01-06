package com.example.productservice.service.impl;

import com.example.commonlib.dto.PagingResponse;
import com.example.productservice.dto.request.BrandRequest;
import com.example.productservice.dto.response.BrandResponse;
import com.example.productservice.dto.response.CategoryResponse;
import com.example.productservice.entity.Brand;
import com.example.productservice.entity.Category;
import com.example.productservice.mapper.BrandMapper;
import com.example.productservice.repository.BrandRepository;
import com.example.productservice.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public BrandResponse create(BrandRequest request) {
        Brand brand = Brand.builder()
                .name(request.getName())
                .build();
        brand = brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }

    @Override
    public BrandResponse getById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        return brandMapper.toResponse(brand);
    }

    @Override
    public  PagingResponse<BrandResponse> getBrands(
            Integer page,
            Integer size,
            String keyword,
            Boolean isDesc
            ) {
        Sort sort = Boolean.TRUE.equals(isDesc)
                ? Sort.by("name").descending()
                : Sort.by("name").ascending();

        Specification<Brand> spec = (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%"
            );
        };

        if (page == null || size == null) {
            List<Brand> brands = brandRepository.findAll(spec, sort);

            return PagingResponse.<BrandResponse>builder()
                    .items(brands.stream()
                            .map(brandMapper::toResponse)
                            .toList())
                    .total(brands.size())
                    .build();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Brand> brandPage =
                brandRepository.findAll(spec, pageable);

        return PagingResponse.<BrandResponse>builder()
                .items(brandPage.getContent()
                        .stream()
                        .map(brandMapper::toResponse)
                        .toList())
                .total(brandPage.getTotalElements())
                .page(page)
                .size(size)
                .hasNext(brandPage.hasNext())
                .hasPrev(brandPage.hasPrevious())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        brand.setName(request.getName());
        brand = brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void delete(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found");
        }
        brandRepository.deleteById(id);
    }
}
