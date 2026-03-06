package com.example.productservice.entity;

import com.example.productservice.util.BaseEntityWithSlug;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Brand extends BaseEntityWithSlug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @OneToMany(
            mappedBy = "brand",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    List<Product> products = new ArrayList<>();

    String paymentMethhod;

    @Override
    protected String getSlugSource() {
        return name;
    }

}
