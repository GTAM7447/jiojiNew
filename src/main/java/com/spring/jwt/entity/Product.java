package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_name", columnList = "productName"),
        @Index(name = "idx_products_type", columnList = "productType"),
        @Index(name = "idx_products_active", columnList = "active")
})
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String description1;

    @Column(columnDefinition = "TEXT")
    private String description2;

    private Double price;

    private Double offers;

    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ProductType {
        SEED, FERTILIZER
    }
}
