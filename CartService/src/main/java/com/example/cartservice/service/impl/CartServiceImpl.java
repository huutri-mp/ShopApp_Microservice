package com.example.cartservice.service.impl;

import com.example.cartservice.dto.requets.AddCartItemRequest;
import com.example.cartservice.dto.requets.RemoveCartItemRequest;
import com.example.cartservice.dto.response.CartResponse;
import com.example.cartservice.dto.response.ProductResponse;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.cartservice.mapper.CartMapper;
import com.example.cartservice.repository.gRPCClient.ProductGrpcClient;
import com.example.commonlib.dto.OrderEvent;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.cartservice.repository.CartItemRepository;
import com.example.cartservice.repository.CartRepository;
import com.example.cartservice.service.CartService;
import com.example.cartservice.utill.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductGrpcClient productClient;
    private final CartMapper cartMapper;

    public CartResponse getCarts() {
        try {
            Integer userId = SecurityUtil.getCurrentUserId();
            if (userId == null) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }

            Cart cart = cartRepository.findByUserId(userId);
            if (cart == null) {
                return CartResponse.builder().build();
            }
            int totalQuantity = cart.getCartItems()
                    .stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();

            BigDecimal totalPrice = cart.getCartItems()
                    .stream()
                    .map(item -> item.getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            CartResponse response = cartMapper.toResponse(cart);
            response.setTotalQuantity(totalQuantity);
            response.setTotalPrice(totalPrice);

            return response;

        } catch (AppException e) {
            throw e;
        }
    }
    public void addToCart(AddCartItemRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        ProductResponse productResponse =
                productClient.getProducts(request.getSkuCode());

        if (productResponse == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setCartItems(new ArrayList<>());
        }

        CartItem existingItem = null;

        for (CartItem item : cart.getCartItems()) {
            if (item.getProductId() == (request.getProductId())
                    && Objects.equals(item.getSkuCode(), request.getSkuCode())) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem item = new CartItem();
            item.setProductId(request.getProductId());
            item.setQuantity(request.getQuantity());
            item.setSkuCode(request.getSkuCode());
            item.setName(productResponse.getName());
            item.setImageUrl(productResponse.getImageUrl());
            item.setPrice(productResponse.getPrice());
            item.setCart(cart);
            cart.getCartItems().add(item);
        }

        cartRepository.save(cart);
    }


    public String removeItem(RemoveCartItemRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        CartItem cartItem = cartItemRepository.findCartItemById(request.getCartItemId());
        if (cartItem == null) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        if (request.getQuantity() > cartItem.getQuantity()) {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }

        if (request.getQuantity() == cartItem.getQuantity()) {
            cartItemRepository.delete(cartItem);
        }
        else{
            cartItem.setQuantity(cartItem.getQuantity() - request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        return "Removed successfully";
    }

    @Transactional
    public String clear() {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            throw new AppException(ErrorCode.CART_NOT_FOUND);
        }


        try {
            cartItemRepository.deleteByCartId(cart.getId());
            cartRepository.save(cart);
            return ("Remove successfully" );

        } catch (Exception e) {
            throw new AppException(ErrorCode.OPERATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void handleCreateOrder(OrderEvent event) {
        Map<String, Object> data = event.getData();
        List<Map<String, Object>> products =
                (List<Map<String, Object>>) data.get("products");

        List<String> skuCodes = products.stream()
                .map(p -> (String) p.get("skuCode"))
                .toList();

        cartItemRepository.deleteBySkuCodeIn(skuCodes);

    }
}
