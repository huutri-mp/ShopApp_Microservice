package com.example.orderservice.repository.gRPCClient;

import com.example.orderservice.dto.response.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import product.Product;
import product.ProductServiceGrpc;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class ProductGrpcClient {
    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;

    public List<ProductResponse> getProducts(List<String> skuCodes) {
        log.info("ProductGrpcClient getProduct, skuCode={}", skuCodes);
        try {

            Product.GetProductsRequest grpcRequest = Product.GetProductsRequest.newBuilder()
                    .addAllSkuCodes(skuCodes)
                    .build();

            Product.GetProductsResponse response = productServiceStub.getProducts(grpcRequest);
            log.info("ProductGrpcClient getProducts response: {}", response);

            if (response.getProductsCount() == 0) {
                return List.of();
            }

            return response.getProductsList().stream()
                    .map(p -> ProductResponse.builder()
                            .productId(p.getProductId())
                            .skuCode(p.getSkuCode())
                            .name(p.getName())
                            .price(BigDecimal.valueOf(p.getPrice()))
                            .imageUrl(p.getImage())
                            .build())
                    .toList();

        } catch (Exception e) {
            log.error("Error when calling product service", e);
            throw e;
        }
    }
}
