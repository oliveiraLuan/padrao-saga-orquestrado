package com.luandeoliveira.inventory_service.core.consumer;

import com.luandeoliveira.inventory_service.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryConsumer {

    private final JsonUtil jsonUtil;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-success}"
    )
    public void consumeSuccessEvent(String payload){
        log.info("Consumindo evento do tópico inventory-success com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.inventory-fail}"
    )
    public void consumeRollbackEvent(String payload){
        log.info("Consumindo evento de rollback do tópico inventory-fail com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }
}
