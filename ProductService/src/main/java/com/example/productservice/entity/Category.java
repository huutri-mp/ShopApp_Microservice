package com.example.productservice.entity;

import com.example.productservice.util.BaseEntityWithSlug;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends BaseEntityWithSlug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name;

    @Override
    protected String getSlugSource() {
        return name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    Category parent;

    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "parent")
    List<Category> children = new ArrayList<>();
}

