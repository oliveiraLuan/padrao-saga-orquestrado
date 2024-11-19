package com.luandeoliveira.orchestrator_service.core.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SagaOrchestratorProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendEvent(String payload, String topic){
        try{
            log.info("Enviando evento para o tópico {} com o payload {}", topic, payload);
            kafkaTemplate.send(topic, payload);
        }catch (Exception e){
            log.error("Falha no envio do evento para o tópico {} com payload {}", topic, payload);
        }
    }
}
