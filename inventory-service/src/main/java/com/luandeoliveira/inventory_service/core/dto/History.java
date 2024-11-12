package com.luandeoliveira.inventory_service.core.dto;

import com.luandeoliveira.inventory_service.enums.EventSource;
import com.luandeoliveira.inventory_service.enums.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
    private EventSource source;
    private SagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
