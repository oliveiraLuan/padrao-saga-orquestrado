package com.luandeoliveira.orchestrator_service.core.consumer;


import com.luandeoliveira.orchestrator_service.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorConsumer {

    private final JsonUtil jsonUtil;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.start-saga}"
    )
    public void consumeEventStartSaga(String payload){
        log.info("Consumindo evento do tópico start-saga com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-success}"
    )
    public void consumeEventFinishSuccess(String payload){
        log.info("Consumindo evento do tópico finish-success com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.finish-fail}"
    )
    public void consumeEventFinishFail(String payload){
        log.info("Consumindo evento do tópico finish-fail com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.start-saga}"
    )
    public void consumeEventOrchestrator(String payload){
        log.info("Consumindo evento do tópico orchestrator com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        log.info(event.toString());
    }
}
