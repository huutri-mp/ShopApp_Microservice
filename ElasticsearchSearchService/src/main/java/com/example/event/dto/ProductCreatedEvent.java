package com.example.event.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreatedEvent {
    int id;
    String name;
    String description;
    Double price;
    String brand;
    String category;
    String status;
}
