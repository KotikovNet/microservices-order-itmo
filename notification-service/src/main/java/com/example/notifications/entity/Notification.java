package com.example.notifications.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long orderId;
    @Column(nullable = false, length = 254)
    private String recipientEmail;
    @Column(nullable = false, length = 1000)
    private String message;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Notification() {
    }

    public Notification(Long userId, Long orderId, String recipientEmail, String message) {
        this.userId = userId;
        this.orderId = orderId;
        this.recipientEmail = recipientEmail;
        this.message = message;
        this.status = "SENT";
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
