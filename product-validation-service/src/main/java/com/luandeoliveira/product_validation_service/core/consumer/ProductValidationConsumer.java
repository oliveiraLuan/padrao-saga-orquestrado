package com.luandeoliveira.product_validation_service.core.consumer;


import com.luandeoliveira.product_validation_service.core.utils.JsonUtil;
import com.luandeoliveira.product_validation_service.service.ProductValidationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ProductValidationConsumer {

    private final JsonUtil jsonUtil;
    private final ProductValidationService productValidationService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-validation-success}"
    )
    public void consumeSuccessEvent(String payload){
        log.info("Consumindo evento do tópico product-validation-success com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        productValidationService.validateExistingProducts(event);
    }
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.product-validation-fail}"
    )
    public void consumeRollbackEvent(String payload){
        log.info("Consumindo evento de rollback do tópico product-validation-fail com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        productValidationService.rollbackEvent(event);
    }
}
