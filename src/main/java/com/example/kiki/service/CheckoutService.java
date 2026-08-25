package com.example.kiki.service;

import com.example.kiki.dto.order.OrderResponseDto;
import com.example.kiki.entity.*;
import com.example.kiki.exception.EmptyCartException;
import com.example.kiki.exception.ResourceNotFoundException;
import com.example.kiki.repository.CartRepository;
import com.example.kiki.repository.OrderRepository;
import com.example.kiki.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponseDto checkout(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Cart cart = cartRepository.findByUserWithItemsAndProducts(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + username));

        if(cart.getItems().isEmpty()){
            throw new EmptyCartException("Cannot checkout an empty cart");
        }

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for(CartItem cartItem : cart.getItems()){
            Product product = cartItem.getProduct();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setOrganization(product.getOrganization());
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubTotal(subtotal);

            orderItems.add(orderItem);
            total = total.add(subtotal);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return new OrderResponseDto(savedOrder.getId(), savedOrder.getTotalAmount(), savedOrder.getCreatedAt());
    }
}
