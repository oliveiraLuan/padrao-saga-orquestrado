package com.luandeoliveira.order_service.core.repository;

import com.luandeoliveira.order_service.core.document.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findAllByCreatedAtDesc();
    Optional<Event> findTop1ByOrderIdOrderByCreatedAtDesc(String orderId);
    Optional<Event> findTop1ByTransactionIdOrderByCreatedAtDesc(String transactionId);
}
