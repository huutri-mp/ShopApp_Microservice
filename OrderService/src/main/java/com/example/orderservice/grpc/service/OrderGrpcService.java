package com.example.orderservice.grpc.service;

import com.example.orderservice.enums.OrderStatus;
import com.example.orderservice.service.OrderService;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import order.Order;
import order.OrderServiceGrpc;


@GrpcService
@RequiredArgsConstructor
@Slf4j
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;

    @Override
    public void updateStatus(Order.UpdateStatusRequest request,
                             StreamObserver<Empty> responseObserver) {

        orderService.updateStatus(request.getOrderId(), OrderStatus.valueOf(request.getStatus()));

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

}
