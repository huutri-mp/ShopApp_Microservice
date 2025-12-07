package com.example.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.validation.constraints.*;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không vượt quá 255 ký tự")
    String name;
    @Size(max = 1000, message = "Mô tả không vượt quá 1000 ký tự")
    String description;
    @NotNull(message = "Giá sản phẩm không được để trống")
    double price;

    @NotNull(message = "Giá gốc không được để trống")
    double originalPrice;
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    int quantity;
    @Size(max = 100, message = "Tên thương hiệu không vượt quá 100 ký tự")
    String brand;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    String status;

    @CreationTimestamp
    @Column(name = "created_at")
    String createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    String updatedAt;

}
