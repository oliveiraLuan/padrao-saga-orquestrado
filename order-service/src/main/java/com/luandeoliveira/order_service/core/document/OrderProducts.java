package com.luandeoliveira.order_service.core.document;

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