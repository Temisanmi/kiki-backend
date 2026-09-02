package com.example.kiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_activities")
@NoArgsConstructor
@Getter
@Setter
public class CartActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_activity_seq")
    @SequenceGenerator(name = "cart_activity_seq", sequenceName = "cart_activity_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User user;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
