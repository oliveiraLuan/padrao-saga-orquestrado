package com.luandeoliveira.product_validation_service.core.repository;

import com.luandeoliveira.product_validation_service.core.model.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ValidationRepository extends JpaRepository<Validation, Integer> {
    Boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
    Optional<Validation> findByOrderIdAndTransactionId(String orderId, String transactionId);
}