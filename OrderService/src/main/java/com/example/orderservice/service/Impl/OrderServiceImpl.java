package com.example.orderservice.service.Impl;

import com.example.commonlib.Enum.MailTemplate;
import com.example.commonlib.dto.NotificationEvent;
import com.example.commonlib.dto.OrderEvent;
import com.example.commonlib.dto.PagingResponse;
import com.example.orderservice.dto.request.OrderItemRequest;
import com.example.orderservice.dto.request.OrderRequest;
import com.example.orderservice.dto.response.*;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderItem;
import com.example.orderservice.enums.OrderStatus;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.orderservice.enums.PaymentMethod;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.gRPCClient.PaymentGrpcClient;
import com.example.orderservice.repository.gRPCClient.ProductGrpcClient;
import com.example.orderservice.repository.gRPCClient.ProfileGrpcClent;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.SecurityUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payment.Payment;
import profile.AddressGrpcResponse;
import profile.ProfileGrpcResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductGrpcClient productClient;

    private final ProfileGrpcClent profileClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final OrderMapper orderMapper;

    private final PaymentGrpcClient paymentClient;

    @PreAuthorize("hasRole('ADMIN')")
    public PagingResponse<OrderResponse> queryOrders (
            Integer page,
            Integer size,
            String createdAtTo,
            String createdAtFrom,
            String orderStatus,
            String paymentMethod) {

        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (orderStatus != null) {

                predicates.add(cb.equal(root.get("status"), OrderStatus.valueOf(orderStatus)));
            }

            if (paymentMethod != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), PaymentMethod.valueOf(paymentMethod)));
            }

            if(createdAtTo != null) {
                if(createdAtFrom != null) {
                    predicates.add(
                            cb.between(root.get("createdAt"), LocalDateTime.parse(createdAtTo), LocalDateTime.parse(createdAtFrom))
                    );
                }
                else  {
                    predicates.add(
                            cb.between(root.get("createdAt"), LocalDateTime.parse(createdAtTo), LocalDateTime.now())
                    );
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));

        };

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAll(spec, pageable);

        return PagingResponse.<OrderResponse>builder()
                .items(orders.getContent()
                        .stream()
                        .map(orderMapper::toResponse)
                        .toList())
                .total(orders.getTotalElements())
                .page(page)
                .size(size)
                .hasNext(orders.hasNext())
                .hasPrev(orders.hasPrevious())
                .build();
    };


    public OrderResponse createOrder(OrderRequest request) {

        Integer userId = SecurityUtil.getCurrentUserId();
        AddressGrpcResponse addressGrpcResponse =  profileClient.getAddressById(request.getShippingAddress());

        Map<String, String> address = new HashMap<>();
        address.put("addressId", String.valueOf(addressGrpcResponse.getAddressId()));
        address.put("contactName", addressGrpcResponse.getContactName());
        address.put("contactPhone", addressGrpcResponse.getContactPhone());
        address.put("addressLine", addressGrpcResponse.getAddressLine());
        address.put("wards", addressGrpcResponse.getWards());
        address.put("province", addressGrpcResponse.getProvince());
        address.put("country", addressGrpcResponse.getCountry());

        List <String> skuCodes = request.getItems().stream().map(OrderItemRequest::getSkuCode).toList();
        List<ProductResponse> productResponses = productClient.getProducts(skuCodes);

        Map<String, Integer> quantityMap =
                request.getItems().stream()
                        .collect(Collectors.toMap(
                                OrderItemRequest::getSkuCode,
                                OrderItemRequest::getQuantity
                        ));


        Order order = new Order();
        order.setUserId(userId);
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());

        BigDecimal totalPrice = BigDecimal.ZERO;

        order.setTotalAmount(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());

        List<Map<String, Object>> products = new ArrayList<>();
        for (ProductResponse product : productResponses) {

            Integer quantity = quantityMap.get(product.getSkuCode());
            if (quantity == null) {
                throw new AppException(ErrorCode.INVALID_QUANTITY);
            }

            OrderItem item = new OrderItem();
            item.setProductId(product.getProductId());
            item.setSkuCode(product.getSkuCode());
            item.setProductName(product.getName());
            item.setPriceAtAdded(product.getPrice());
            item.setQuantity(quantity);
            item.setImageUrl(product.getImageUrl());

            totalPrice = totalPrice.add(
                    product.getPrice().multiply(BigDecimal.valueOf(quantity))
            );

            order.addItem(item);


            // Event noti
            Map<String, Object> productData = new HashMap<>();
            productData.put("name",product.getName());
            productData.put("skuCode",product.getSkuCode());
            productData.put("quantity", quantity);
            productData.put("price", product.getPrice());
            productData.put(
                    "totalPrice",
                    item.getPriceAtAdded().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
            products.add(productData);
        }

        order.setTotalAmount(totalPrice);
        orderRepository.save(order);

        String paymentUrl = "";
        if (request.getPaymentMethod() == PaymentMethod.VNPAY){
            Payment.InitPaymentReqest initPaymentReqest = Payment.InitPaymentReqest.newBuilder()
                    .setAmount(totalPrice.intValue())
                    .setUserId(userId)
                    .setOrderId(order.getId())
                    .setIpAdress(request.getIpAddress())
                    .build();
            paymentUrl = paymentClient.initPayment(initPaymentReqest);
        }

        ProfileGrpcResponse userProdile = profileClient.getProfile(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("fullName", userProdile.getFullName());
        data.put("orderId", order.getId());
        data.put("products", products);
        data.put("orderTotal", totalPrice);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("email")
                .recipient(userProdile.getEmail())
                .template(MailTemplate.ORDER_SUCCESS)
                .data(data)
                .build();

        OrderEvent orderEvent = OrderEvent.builder()
                .data(data)
                .build();
        try {
            kafkaTemplate.send("notification-delivery", notificationEvent);

            kafkaTemplate.send("order-created", orderEvent);
        } catch (Exception e) {
            log.error("Failed to send Kafka notification event", e);
        }


        log.info("Order created successfully with ID: {}", order.getId());
        OrderResponse response = orderMapper.toResponse(order);
        response.setPaymentUrl(paymentUrl);
        return response;
    }

    public List<OrderResponse> getOrders() {
        int userId = SecurityUtil.getCurrentUserId();

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (orders.isEmpty()) {
            return List.of();
        }

        List<OrderResponse> response = new ArrayList<>();

        for (Order order : orders) {
            response.add(orderMapper.toResponse(order));
        }

        return response;
    }


    public String updateStatus(long orderId, OrderStatus status) {

        log.info("Updating order status for order ID: {}", orderId);
        log.info("Updating order status for order ID: {}", status);
        if(status == OrderStatus.CANCELED) {
            return cancleOrder(orderId);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.DELIVERED) {
            log.error("Cannot update status for cancelled or delivered orders");
            throw new AppException(ErrorCode.ORDER_STATUS_UPDATE_NOT_ALLOWED);
        }

        order.setStatus(status);
        orderRepository.save(order);

        log.info("Order status updated to {} for order ID: {}", status, orderId);
        return "Order status updated successfully";
    }
    @Transactional
    public String cancleOrder(long orderId) {

        int userId = SecurityUtil.getCurrentUserId();

        log.info("Cancelling order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if(userId != order.getUserId()) {
            throw new AppException(ErrorCode.ORDER_CANCELLATION_NOT_ALLOWED);
        }
        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.PAID) {
            throw new AppException(ErrorCode.ORDER_CANCELLATION_NOT_ALLOWED);
        }

        // Hoàn lại hàng tồn kho cho các sản phẩm trong đơn hàng
        List<Map<String, Object>> products = new ArrayList<>();
        for(OrderItem item : order.getItems()) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("skuCode",item.getSkuCode());
            productData.put("name",item.getProductName());
            productData.put("quantity", item.getQuantity() * -1);
            productData.put("price", item.getPriceAtAdded());
            productData.put(
                    "totalPrice",
                    item.getPriceAtAdded().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
            products.add(productData);
        }
        ProfileGrpcResponse userProdile = profileClient.getProfile(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("fullName", userProdile.getFullName());
        data.put("orderId", order.getId());
        data.put("products", products);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("email")
                .recipient(userProdile.getEmail())
                .template(MailTemplate.ORDER_CANCLE)
                .data(data)
                .build();

        OrderEvent orderEvent = OrderEvent.builder()
                .data(data)
                .build();

        // Cập nhật trạng thái đơn hàng
        if (order.getPaymentMethod() == PaymentMethod.VNPAY
                && order.getStatus() == OrderStatus.PAID) {

            Payment.RefundPaymentRequest request =
                    Payment.RefundPaymentRequest.newBuilder()
                            .setOrderId(orderId)
                            .build();

            if (!paymentClient.refundPayment(request).getSuccess()) {
                return "Refund failed";
            }
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        log.info("Order with ID {} cancelled successfully", orderId);

        try {
            kafkaTemplate.send("notification-delivery", notificationEvent).get();
            kafkaTemplate.send("order-canceled", orderEvent).get();
        } catch (Exception e) {
            log.error("Kafka send failed", e);
            throw new RuntimeException("Kafka send failed", e);
        }

        return "Order cancelled successfully";
    }

}
