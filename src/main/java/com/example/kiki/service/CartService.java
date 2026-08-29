package com.example.kiki.service;

import com.example.kiki.dto.cart.AddCartItemRequest;
import com.example.kiki.dto.cart.CartItemResponseDto;
import com.example.kiki.dto.cart.CartResponseDto;
import com.example.kiki.dto.cart.UpdateCartItemRequest;
import com.example.kiki.entity.*;
import com.example.kiki.exception.InsufficientStockException;
import com.example.kiki.exception.ResourceNotFoundException;
import com.example.kiki.repository.CartActivityRepository;
import com.example.kiki.repository.CartRepository;
import com.example.kiki.repository.ProductRepository;
import com.example.kiki.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartActivityRepository cartActivityRepository;
    private final ProductRepository productRepository;
    private final CurrentUserProvider currentUserProvider;

    private Cart getCartEntityForUsername(String username) {
        User user = currentUserProvider.getCurrentUser();

        return cartRepository.findByUserWithItemsAndProducts(user)
                .or(()-> cartRepository.findByUser(user))
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + username));
    }

    private CartItemResponseDto toItemResponseDto(CartItem item) {
        Product product = item.getProduct();
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        String organizationName = product.getOrganization() != null
                ? product.getOrganization().getOrgName()
                : "Kiki";

        return new CartItemResponseDto(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                organizationName,
                item.getQuantity(),
                subtotal
        );
    }

    private CartResponseDto toResponseDto(Cart cart) {
        List<CartItemResponseDto> itemDtos = cart.getItems().stream()
                .map(this::toItemResponseDto)
                .toList();

        BigDecimal totalPrice = itemDtos.stream()
                .map(CartItemResponseDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDto(cart.getId(), itemDtos, totalPrice);
    }

    public CartResponseDto getCartForUser(String username) {
        Cart cart = getCartEntityForUsername(username);
        return toResponseDto(cart);
    }

    public CartResponseDto addItemToCart(String username, AddCartItemRequest request) {
        Cart cart = getCartEntityForUsername(username);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        int alreadyInCart = existingItem != null ? existingItem.getQuantity() : 0;
        int desiredTotal = alreadyInCart + request.getQuantity();

        if (desiredTotal > product.getStockQuantity()) {
            throw new InsufficientStockException("Only " + product.getStockQuantity() + " unit(s) of \"" + product.getName() + "\" left in stock");
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);

        CartActivity activity = new CartActivity();
        activity.setProduct(product);
        activity.setOrganization(product.getOrganization());
        activity.setUser(cart.getUser());
        activity.setQuantity(request.getQuantity());
        cartActivityRepository.save(activity);

        return toResponseDto(savedCart);
    }

    public CartResponseDto updateItemQuantity(String username, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getCartEntityForUsername(username);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        Integer stock = item.getProduct().getStockQuantity();
        if (request.getQuantity() > stock) {
            throw new InsufficientStockException(
                    "Only " + stock + " unit(s) of \"" + item.getProduct().getName() + "\" left in stock");
        }

        item.setQuantity(request.getQuantity());

        Cart savedCart = cartRepository.save(cart);
        return toResponseDto(savedCart);
    }

    public CartResponseDto removeItemFromCart(String username, Long itemId) {
        Cart cart = getCartEntityForUsername(username);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        cart.getItems().remove(item);

        Cart savedCart = cartRepository.save(cart);
        return toResponseDto(savedCart);
    }
}