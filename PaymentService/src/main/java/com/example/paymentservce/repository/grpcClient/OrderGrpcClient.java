package com.example.paymentservce.repository.grpcClient;

import com.example.paymentservce.enums.OrderStatus;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import order.Order;
import order.OrderServiceGrpc;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderGrpcClient {

    @GrpcClient("order-service")
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    public void updateStatus(long orderId, OrderStatus orderStatus) {

        try {
            Order.UpdateStatusRequest request =
                    Order.UpdateStatusRequest.newBuilder()
                            .setOrderId(orderId)
                            .setStatus(orderStatus.name())
                            .build();

            orderServiceBlockingStub.updateStatus(request);

            log.info("Updated order status successfully for orderId={}", orderId);

        } catch (Exception e) {
            log.error("Failed to update order status via gRPC", e);
            throw e;
        }
    }

}

