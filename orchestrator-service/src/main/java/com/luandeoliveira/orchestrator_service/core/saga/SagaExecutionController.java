package com.luandeoliveira.orchestrator_service.core.saga;

import com.luandeoliveira.orchestrator_service.core.dto.Event;
import com.luandeoliveira.orchestrator_service.core.enums.Topics;
import com.luandeoliveira.orchestrator_service.exceptions.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.luandeoliveira.orchestrator_service.core.saga.SagaHandler.*;
import static java.lang.String.format;
import static org.springframework.util.ObjectUtils.isEmpty;

@Component
@Slf4j
@AllArgsConstructor
public class SagaExecutionController {

    private static final String SAGA_LOG_ID = "ORDER ID: %s | TRANSACTION: ID %s | EVENT ID: %s";

    public Topics getNextTopic(Event event){
        if(isEmpty(event.getStatus()) || isEmpty(event.getSource()))
            throw new ValidationException("Status e Origem precisam estar preenchidos");
        var topic = findTopicBySourceAndStatus(event);
        logCurrentSaga(event, topic);
        return topic;
    }

    private Topics findTopicBySourceAndStatus(Event event){
        return (Topics) (Arrays.stream(SAGA_HANDLER)
                .filter(row -> isSourceAndStatus(event, row))
                .map(i -> i[TOPIC_INDEX])
                .findFirst()
                .orElseThrow(() -> new ValidationException("Não foi encontrado tópico para o status e origem informados.")));
    }

    private Boolean isSourceAndStatus(Event event, Object[] row){
        var source = row[EVENT_SOURCE_INDEX];
        var status = row[SAGA_STATUS_INDEX];
        return event.getSource().equals(source) && event.getStatus().equals(status);
    }

    private void logCurrentSaga(Event event, Topics nextTopic) {
        var sagaId = createSagaId(event);
        var source = event.getSource();
        switch (event.getStatus()){
            case SUCCESS -> log.info("### SAGA ATUAL: {} | SUCCESS | PRÓXIMO TÓPICO: {} | {}", source, nextTopic, sagaId);
            case ROLLBACK_PENDING -> log.info("### SAGA ATUAL: {} | ENVIANDO PARA ROLLBACK | PRÓXIMO TÓPICO: {} | {}", source, nextTopic, sagaId);
            case FAIL -> log.info("### SAGA ATUAL: {} | FAIL | PRÓXIMO TÓPICO: {} | {}", source, nextTopic, sagaId);
        }
    }

    private String createSagaId(Event event) {
        return format(SAGA_LOG_ID, event.getPayload().getId(), event.getTransactionId(), event.getId());
    }
}