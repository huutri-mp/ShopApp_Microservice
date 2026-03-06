package com.example.cartservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "sku_code", nullable = false)
    String skuCode;

    @Column(name = "product_id", nullable = false)
    long productId;

    String name;

    String imageUrl;

    int quantity;

    BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    Cart cart;

}
