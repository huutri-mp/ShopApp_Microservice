package com.example.productservice.gRPC;

import com.example.productservice.entity.ProductImage;
import com.example.productservice.entity.ProductVariant;
import com.example.productservice.service.ProductService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;

import product.ProductServiceGrpc;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    @Override
    public void getProducts(product.Product.GetProductsRequest request,
                            StreamObserver<product.Product.GetProductsResponse> responseObserver) {
        try {
            log.info("gRPC getProducts - Received request, items={}");
            List<String> skuCodes = request.getSkuCodesList();

            List<ProductVariant> products = productService.getProductsForOrder(skuCodes);

            product.Product.GetProductsResponse.Builder respBuilder =
        product.Product.GetProductsResponse.newBuilder();

            for (ProductVariant pv : products) {
                log.info("gRPC getProducts - Adding product: {}", pv.getSalePrice());
                ProductImage firstImage = (pv.getProduct().getImages() == null || pv.getProduct().getImages().isEmpty())
                        ? null
                        : pv.getProduct().getImages().get(0);

                respBuilder.addProducts(
                        product.Product.ProductInfo.newBuilder()
                                .setProductId(pv.getProduct().getId() == null ? 0L : pv.getProduct().getId())
                                .setSkuCode(pv.getSkuCode() == null || pv.getSkuCode() == null ? "" : pv.getSkuCode())
                                .setName(pv.getProduct().getName() == null ? "" : pv.getProduct().getName())
                                .setPrice(toLongSafe(
                                        pv == null
                                                ? null
                                                : (pv.getSalePrice() != null ? pv.getSalePrice() : pv.getPrice())
                                ))
                                .setImage(firstImage == null ? "" : firstImage.getUrl())
                                .build()
                );
            }

            responseObserver.onNext(respBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC getProducts - Failed to get products", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private long toLongSafe(BigDecimal value) {
        if (value == null) return 0L;
        try {
            return value.longValueExact();
        } catch (ArithmeticException ex) {
            return value.longValue();
        }
    }
}
