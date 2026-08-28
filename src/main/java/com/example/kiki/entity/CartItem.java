package com.example.kiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@Getter
@Setter
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_seq")
    @SequenceGenerator(name = "cart_item_seq", sequenceName = "cart_item_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
}