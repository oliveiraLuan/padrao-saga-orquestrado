package com.luandeoliveira.order_service.core.service;

import com.luandeoliveira.order_service.core.document.Event;
import com.luandeoliveira.order_service.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private EventRepository eventRepository;

    public void notifyEnding(Event event){
        event.setOrderId(event.getPayload().getId());
        event.setCreatedAt(LocalDateTime.now());
        save(event);
        log.info("Pedido com {} saga notificado! Transaction id: {}", event.getPayload(), event.getTransactionId());
    }

    public Event save(Event event){
        return eventRepository.save(event);
    }
}