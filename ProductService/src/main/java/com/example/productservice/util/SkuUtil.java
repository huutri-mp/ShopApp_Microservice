package com.example.productservice.util;

import com.example.productservice.entity.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SkuUtil  {

    public static String generateSku(Product p) {
        return p.getName()
                .replaceAll("\\s+", "")
                .toUpperCase()
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }


}
