package com.luandeoliveira.order_service.core.service;

import com.luandeoliveira.order_service.core.document.Event;
import com.luandeoliveira.order_service.core.dto.EventFilters;
import com.luandeoliveira.order_service.core.repository.EventRepository;
import com.luandeoliveira.order_service.exceptions.ValidationException;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<Event> findAll(){
        return eventRepository.findAllByOrderByCreatedAtDesc();
    }

    public Event findByFilters(EventFilters filters) {
        if (isFiltersEmpty(filters))
            throw new ValidationException("Filters must be informed");

        if (StringUtils.isNotBlank(filters.getOrderId()))
            return findByOrderId(filters.getOrderId());

        return findByTransactionId(filters.getTransactionId());
    }

    public Event findByOrderId(String orderId){
        return eventRepository.findTop1ByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new ValidationException("Event with this orderId not found."));
    }

    public Event findByTransactionId(String transactionId){
        return eventRepository.findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId)
                .orElseThrow(() -> new ValidationException("Event with this transactionId not found."));
    }

    public Boolean isFiltersEmpty(EventFilters filters){
        return StringUtils.isBlank(filters.getOrderId()) && StringUtils.isBlank(filters.getTransactionId());
    }
}