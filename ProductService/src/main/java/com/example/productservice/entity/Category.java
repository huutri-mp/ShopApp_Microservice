package com.example.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @NotBlank(message = "Tên loại sản phẩm không được để trống")
    @Size(max = 255, message = "Tên loại sản phẩm không vượt quá 255 ký tự")
    String name;
    @Size(max = 1000, message = "Mô tả không vượt quá 1000 ký tự")
    String description;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDate createdAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Product> products;
}
