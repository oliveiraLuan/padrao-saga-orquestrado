package com.luandeoliveira.payment_service.core.model;

import com.luandeoliveira.payment_service.core.enums.EPaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String orderId;
    @Column(nullable = false)
    private String transactionId;
    @Column(nullable = false)
    private int totalItems;
    @Column(nullable = false)
    private double totalAmount;
    @Column(nullable = false)
    private EPaymentStatus status;
    @Column(nullable = false)
    private boolean success;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        var now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        status = EPaymentStatus.PENDING;
    }
    @PreUpdate
    public void preUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
