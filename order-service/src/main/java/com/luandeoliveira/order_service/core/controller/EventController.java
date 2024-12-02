package com.luandeoliveira.order_service.core.controller;

import com.luandeoliveira.order_service.core.document.Event;
import com.luandeoliveira.order_service.core.dto.EventFilters;
import com.luandeoliveira.order_service.core.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/api/event")
@RestController
public class EventController {
    private final EventService eventService;

    @GetMapping
    public Event findByFilters(EventFilters filters){
        return eventService.findByFilters(filters);
    }

    @GetMapping("/all")
    public List<Event> findAll(){
        return eventService.findAll();
    }
}
