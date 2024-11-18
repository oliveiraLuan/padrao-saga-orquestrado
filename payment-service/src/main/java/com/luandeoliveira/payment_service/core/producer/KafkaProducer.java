package com.luandeoliveira.payment_service.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.orchestrator}")
    private String orchestratorTopic;

    public void sendEvent(String payload){
        try {
            log.info("Enviando evento para o tópico {} com o payload {}", orchestratorTopic, payload);
            kafkaTemplate.send(orchestratorTopic, payload);
        }catch (Exception e){
            log.error("Falha no envio do evento para o tópico {} com payload {}", orchestratorTopic, payload);
        }
    }
}
