package com.example.cartservice.repository.gRPCClient;

import com.example.cartservice.dto.response.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import product.Product;
import product.ProductServiceGrpc;

import java.math.BigDecimal;

@Service
@Slf4j
public class ProductGrpcClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productServiceStub;

    public ProductResponse getProducts(String skuCode) {
        log.info("ProductGrpcClient getProduct, skuCode={}", skuCode);
        try {

            Product.GetProductsRequest grpcRequest = Product.GetProductsRequest.newBuilder()
                    .addSkuCodes(skuCode)
                    .build();


            Product.GetProductsResponse response = productServiceStub.getProducts(grpcRequest);

            if (response.getProductsCount() == 0) {
                return null;
            }

            Product.ProductInfo info = response.getProducts(0);

            return ProductResponse.builder()
                    .name(info.getName())
                    .price(BigDecimal.valueOf(info.getPrice()))
                    .imageUrl(info.getImage())
                    .build();
        } catch (Exception e) {
            log.error("Error when calling product service for, skuCode: {}", skuCode, e);
            throw e;
        }
    }
}
