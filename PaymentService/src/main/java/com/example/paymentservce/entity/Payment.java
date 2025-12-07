package com.example.paymentservce.entity;

import com.example.paymentservce.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "payments")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "order_id", nullable = false)
    int orderId;

    @Column(name = "amount", nullable = false)
    double amount;

    @Column(name = "payment_method", nullable = false, length = 50)
    PaymentMethod paymentMethod;

}
