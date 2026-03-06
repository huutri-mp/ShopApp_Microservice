package com.example.paymentservce.repository;

import com.example.paymentservce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Payment, Integer> {
}
