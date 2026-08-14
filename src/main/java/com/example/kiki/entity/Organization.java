package com.example.kiki.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "org_seq")
    @SequenceGenerator(name = "org_seq", sequenceName = "organization_sequence", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String orgName;

    private String orgDescription; //main organization description or organization product description??

    private String logoUrl;

    private boolean verified;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
