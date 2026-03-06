package com.example.orderservice.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "order_items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    Order order;

    @Column(name = "product_id", nullable = false)
    Long productId;

    @Column(name = "sku_code", nullable = false)
    String skuCode;

    String productName;

    String imageUrl;

    Integer quantity;

    @Column(name = "price_at_added")
    BigDecimal priceAtAdded;

}
