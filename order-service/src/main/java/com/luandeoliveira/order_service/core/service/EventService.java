package com.luandeoliveira.order_service.core.service;

import com.luandeoliveira.order_service.core.document.Event;
import com.luandeoliveira.order_service.core.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventService {

    private EventRepository eventRepository;

    public Event save(Event event){
        return eventRepository.save(event);
    }
}