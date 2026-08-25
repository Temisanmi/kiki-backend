package com.example.kiki.controller;

import com.example.kiki.dto.cart.AddCartItemRequest;
import com.example.kiki.dto.cart.CartResponseDto;
import com.example.kiki.dto.cart.UpdateCartItemRequest;
import com.example.kiki.dto.order.OrderResponseDto;
import com.example.kiki.service.CartService;
import com.example.kiki.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CheckoutService checkoutService;

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCartForUser(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(authentication.getName(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(authentication.getName(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDto> removeItemFromCart(
            Authentication authentication,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(authentication.getName(), itemId));
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDto> checkout(Authentication authentication) {
        OrderResponseDto order = checkoutService.checkout(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}