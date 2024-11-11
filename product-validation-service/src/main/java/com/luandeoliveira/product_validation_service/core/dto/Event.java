package com.luandeoliveira.product_validation_service.core.dto;

import com.luandeoliveira.product_validation_service.core.enums.EventSource;
import com.luandeoliveira.product_validation_service.core.enums.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String id;
    private String transactionId;
    private String orderId;
    private Order payload;
    private List<History> eventHistory;
    private LocalDateTime createdAt;
    private SagaStatus status;
    private EventSource source;
}