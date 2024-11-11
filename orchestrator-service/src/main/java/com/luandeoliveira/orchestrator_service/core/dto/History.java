package com.luandeoliveira.orchestrator_service.core.dto;

import com.luandeoliveira.orchestrator_service.core.enums.EventSource;
import com.luandeoliveira.orchestrator_service.core.enums.SagaStatus;
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
