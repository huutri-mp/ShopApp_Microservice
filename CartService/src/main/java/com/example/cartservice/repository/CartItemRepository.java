package com.example.cartservice.repository;

import com.example.cartservice.dto.response.CartResponse;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByCartId (int cartId);
    CartItem findCartItemById(int id);
    void deleteByCartId(int cartId);
    void deleteBySkuCodeIn(List<String> skuCodes);
}
