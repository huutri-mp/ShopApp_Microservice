//package com.example.productservice.service.impl;
//
//import com.example.commonlib.dto.PagingResponse;
//import com.example.productservice.dto.response.ProductImageResponse;
//import com.example.productservice.entity.Product;
//import com.example.productservice.entity.ProductImage;
//import com.example.productservice.mapper.ProductImageMapper;
//import com.example.productservice.repository.ProductImageRepository;
//import com.example.productservice.repository.ProductRepository;
//import com.example.productservice.service.ProductImageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ProductImageServiceImpl implements ProductImageService {
//
//    private final ProductImageRepository imageRepository;
//    private final ProductRepository productRepository;
//    private final ProductImageMapper imageMapper;
//
//    @Override
//    public ProductImageResponse create(ProductImageRequest request, Long productId) {
//        Product product = null;
//        if (productId != null) {
//            product = productRepository.findById(productId)
//                    .orElseThrow(() -> new RuntimeException("Product not found"));
//        }
//        ProductImage image = ProductImage.builder()
//                .url(request.getUrl())
//                .product(product)
//                .build();
//        image = imageRepository.save(image);
//        return imageMapper.toResponse(image);
//    }
//
//    @Override
//    public ProductImageResponse getById(Long id) {
//        ProductImage image = imageRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Image not found"));
//        return imageMapper.toResponse(image);
//    }
//
//    @Override
//    public PagingResponse<ProductImageResponse> getAll(int page, int size) {
//        Page<ProductImage> p = imageRepository.findAll(PageRequest.of(page, size));
//        return PagingResponse.<ProductImageResponse>builder()
//                .items(p.getContent().stream().map(imageMapper::toResponse).toList())
//                .total(p.getTotalElements())
//                .page(page)
//                .size(size)
//                .hasNext(p.hasNext())
//                .hasPrev(p.hasPrevious())
//                .build();
//    }
//
//    @Override
//    public ProductImageResponse update(Long id, ProductImageRequest request, Long productId) {
//        ProductImage image = imageRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Image not found"));
//        image.setUrl(request.getUrl());
//        if (productId != null) {
//            Product product = productRepository.findById(productId)
//                    .orElseThrow(() -> new RuntimeException("Product not found"));
//            image.setProduct(product);
//        }
//        image = imageRepository.save(image);
//        return imageMapper.toResponse(image);
//    }
//
//    @Override
//    public void delete(Long id) {
//        if (!imageRepository.existsById(id)) {
//            throw new RuntimeException("Image not found");
//        }
//        imageRepository.deleteById(id);
//    }
//}
