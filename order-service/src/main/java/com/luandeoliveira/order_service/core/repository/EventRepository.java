package com.luandeoliveira.order_service.core.repository;

import com.luandeoliveira.order_service.core.document.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {
}
