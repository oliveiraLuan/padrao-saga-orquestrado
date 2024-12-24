package com.luandeoliveira.orchestrator_service.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProducts {
    private Product product;
    private int quantity;
}