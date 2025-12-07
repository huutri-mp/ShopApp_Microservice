package com.example.cartservice.service.impl;

import com.example.cartservice.dto.requets.AddCartItemRequest;
import com.example.cartservice.dto.requets.InternalRemoveCartItemRequest;
import com.example.cartservice.dto.requets.RemoveCartItemRequest;
import com.example.cartservice.dto.response.CartResponse;
import com.example.cartservice.dto.response.InternalCartItemResponse;
import com.example.cartservice.dto.response.InternalCartResponse;
import com.example.cartservice.dto.response.ProductResponse;
import com.example.cartservice.entity.Cart;
import com.example.cartservice.entity.CartItem;
import com.example.commonlib.exception.AppException;
import com.example.commonlib.exception.ErrorCode;
import com.example.cartservice.repository.CartItemRepository;
import com.example.cartservice.repository.CartRepository;
import com.example.cartservice.repository.httpClient.ProductClient;
import com.example.cartservice.service.CartService;
import com.example.cartservice.utill.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Primary
public class CartServiceImpl implements CartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductClient productClient;

    public List<CartResponse> getCart(int cartId) {
        try {
            Integer userId = SecurityUtil.getCurrentUserId();
            log.info("Fetching cart for userId: {}, cartId: {}", userId, cartId);
            if (userId == null) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

            if (cart.getUserId() != userId) {
                throw new AppException(ErrorCode.FORBIDDEN);
            }

            List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
            if (cartItems == null || cartItems.isEmpty()) {
                throw new AppException(ErrorCode.CART_EMPTY);
            }

            List<CartResponse> cartResponses = new ArrayList<>();
            for (CartItem item : cartItems) {
                try {
                    ResponseEntity<ProductResponse> response = productClient.getProductById(item.getProductId());
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        ProductResponse productResponse = response.getBody();

                        CartResponse cartResponse = new CartResponse();
                        cartResponse.setProductName(productResponse.getName());
                        cartResponse.setProductPrice(productResponse.getPrice());
                        cartResponse.setQuantity(item.getQuantity());
                        cartResponse.setPriceAtAdded(item.getPriceAtAdded());
                        cartResponse.setTotalPrice(productResponse.getPrice() * item.getQuantity());

                        cartResponses.add(cartResponse);
                    } else {
                        log.error("Failed to get product info for productId: {}", item.getProductId());
                    }
                } catch (Exception e) {
                    log.error("Error when calling product service for productId: {}", item.getProductId(), e);

                }
            }

            if (cartResponses.isEmpty()) {
                throw new AppException(ErrorCode.PRODUCT_INFO_UNAVAILABLE);
            }

            return cartResponses;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in getCart for cartId: {}", cartId, e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String addToCart(AddCartItemRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        log.info("Adding item to cart for userId: {}, request: {}", userId, request);
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart = cartRepository.save(cart);
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());
        if (cartItem != null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPriceAtAdded(productClient.getProductById(request.getProductId()).getBody().getPrice());
        }
        else {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        }
        cartItemRepository.save(cartItem);
        return "Thêm sản phẩm vào giỏ hàng thành công";
    }

    public String removeFromCart(RemoveCartItemRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        CartItem cartItem = cartItemRepository.findCartItemById(request.getCartItemId());
        if (cartItem == null) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        log.info("Removing item from cart for userId: {}, cartId: {}, productId: {}", userId, cartItem.getCart());

        if (cartItem.getCart().getUserId() != userId) {
            throw new AppException(ErrorCode.FORBIDDEN);
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

        return "Xóa sản phẩm khỏi giỏ hàng thành công";
    }

    @Transactional
    public String removeAllFromCart(Integer cartId) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. Kiểm tra và xác thực giỏ hàng
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getUserId() != (userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        try {
            cartItemRepository.deleteByCartId(cartId);
            cartRepository.save(cart);
            return ("Đã xóa thành công sản phẩm khỏi giỏ hàng" );

        } catch (Exception e) {
            log.error("Failed to remove items from cart {}", cartId, e);
            throw new AppException(ErrorCode.OPERATION_FAILED);
        }
    }

    public InternalCartResponse getInternalCart(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new AppException(ErrorCode.CART_NOT_FOUND);
        }

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        return InternalCartResponse.builder()
                .cartId(cart.getId())
                .items(items.stream()
                        .map(item -> InternalCartItemResponse.builder()
                                .cartItemId(item.getId())
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .priceAtAdded(item.getPriceAtAdded())
                                .build())
                        .toList())
                .build();
    }
    public String removeSelectedItemsFromCart(InternalRemoveCartItemRequest request) {
        if (request == null || request.getCartId() == null || request.getProductIds() == null || request.getProductIds().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        List<CartItem> cartItems = cartItemRepository.findByCartId(request.getCartId());

        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        // Lọc các cartItem cần xóa
        List<CartItem> itemsToRemove = cartItems.stream()
                .filter(item -> request.getProductIds().contains(item.getProductId()))
                .toList();

        if (itemsToRemove.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_CART_SELECTION);
        }
        cartItemRepository.deleteAll(itemsToRemove);
        return "Selected items removed from cart successfully.";
    }

}
