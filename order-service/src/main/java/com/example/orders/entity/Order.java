package com.example.orders.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false, length = 500)
    private String description;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 24)
    private String status;
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Order() {
    }

    public Order(Long userId, String description, BigDecimal amount, String status) {
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.status = status;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void update(Long userId, String description, BigDecimal amount, String status) {
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.status = status;
    }
}
