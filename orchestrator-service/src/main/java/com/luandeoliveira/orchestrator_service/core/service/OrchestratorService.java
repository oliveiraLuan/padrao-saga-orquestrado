package com.luandeoliveira.orchestrator_service.core.service;

import com.luandeoliveira.orchestrator_service.core.dto.Event;
import com.luandeoliveira.orchestrator_service.core.dto.History;
import com.luandeoliveira.orchestrator_service.core.enums.EventSource;
import com.luandeoliveira.orchestrator_service.core.enums.SagaStatus;
import com.luandeoliveira.orchestrator_service.core.enums.Topics;
import com.luandeoliveira.orchestrator_service.core.producer.SagaOrchestratorProducer;
import com.luandeoliveira.orchestrator_service.core.saga.SagaExecutionController;
import com.luandeoliveira.orchestrator_service.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class OrchestratorService {
    private final JsonUtil jsonUtil;
    private final SagaExecutionController sagaExecutionController;
    private final SagaOrchestratorProducer producer;

    public void startSaga(Event event){
        event.setSource(EventSource.ORCHESTRATOR);
        event.setStatus(SagaStatus.SUCCESS);
        var topic = getTopic(event);
        log.info("SAGA INICIADA");
        sendEventToProducerWithTopic(event, topic);
        addHistory(event, "Saga Iniciada!");
    }

    public void continueSaga(Event event){
        var topic = getTopic(event);
        log.info("CONTINUANDO SAGA. ENVIANDO PARA O TÓPICO {}", topic.getTopic());
        sendEventToProducerWithTopic(event, topic);
    }

    public void finishFailedSaga(Event event){
        event.setSource(EventSource.ORCHESTRATOR);
        event.setStatus(SagaStatus.FAIL);
        log.info("SAGA FINALIZADA COM ERROS {}", event.getId());
        notifyFinished(event);
        addHistory(event, "Saga finalizada com erros");
    }

    public void finishSuccessSaga(Event event){
        event.setSource(EventSource.ORCHESTRATOR);
        event.setStatus(SagaStatus.SUCCESS);
        log.info("SAGA FINALIZADA COM SUCESSO {}", event.getId());
        notifyFinished(event);
        addHistory(event, "Saga finalizada com sucesso!");
    }

    private void notifyFinished(Event event){
        sendEventToProducerWithTopic(event, Topics.NOTIFY_ENDING);
    }

    private Topics getTopic(Event event) {
        return sagaExecutionController.getNextTopic(event);
    }
    private void sendEventToProducerWithTopic(Event event, Topics topic){
        producer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }
    public void addHistory(Event event, String message){
        History history = History
                .builder()
                .createdAt(LocalDateTime.now())
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .build();
        event.addHistory(history);
    }
}
