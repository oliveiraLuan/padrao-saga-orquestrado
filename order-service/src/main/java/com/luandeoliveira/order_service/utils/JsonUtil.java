package com.luandeoliveira.order_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luandeoliveira.order_service.core.document.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class JsonUtil {
    private final ObjectMapper objectMapper;

    public String toJson(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Não foi possível converter o objeto para json{}", e.getMessage());
            return "";
        }
    }

    public Event toEvent(String json){
        try {
            return objectMapper.readValue(json, Event.class);
        } catch (Exception e){
            log.error("Não foi possível converter o json para Event.class");
            return null;
        }
    }
}
