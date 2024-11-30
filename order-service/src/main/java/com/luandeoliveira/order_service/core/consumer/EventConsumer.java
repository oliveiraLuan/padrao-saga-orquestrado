package com.luandeoliveira.order_service.core.consumer;

import com.luandeoliveira.order_service.core.service.EventService;
import com.luandeoliveira.order_service.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EventConsumer {

    private final JsonUtil jsonUtil;
    private final EventService eventService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.notify-ending}"
    )
    public void consumeEventNotifyEnding(String payload){
        log.info("Consumindo evento Notify-Ending com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }
}
