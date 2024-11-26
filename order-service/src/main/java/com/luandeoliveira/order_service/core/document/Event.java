package com.luandeoliveira.order_service.core.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "event")
public class Event {
    @Id
    private String id;
    private String transactionId;
    private String orderId;
    private Order payload;
    private List<History> eventHistory;
    private LocalDateTime createdAt;
    private String status;
    private String source;
}
