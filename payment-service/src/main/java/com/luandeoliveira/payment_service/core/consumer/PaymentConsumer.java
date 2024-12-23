package com.luandeoliveira.payment_service.core.consumer;


import com.luandeoliveira.payment_service.core.service.PaymentService;
import com.luandeoliveira.payment_service.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentConsumer {

    private final JsonUtil jsonUtil;
    private final PaymentService paymentService;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-success}"
    )
    public void consumeSuccessEvent(String payload){
        log.info("Consumindo evento do tópico payment-success com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        paymentService.realizePayment(event);
    }
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.payment-fail}"
    )
    public void consumeRollbackEvent(String payload){
        log.info("Consumindo evento de rollback do tópico payment-fail com payload: {}", payload);
        var event = jsonUtil.toEvent(payload);
        paymentService.realizeRefund(event);
    }
}
