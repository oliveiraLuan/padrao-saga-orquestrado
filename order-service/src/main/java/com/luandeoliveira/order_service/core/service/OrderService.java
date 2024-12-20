package com.luandeoliveira.order_service.core.service;

import com.luandeoliveira.order_service.core.document.Event;
import com.luandeoliveira.order_service.core.document.Order;
import com.luandeoliveira.order_service.core.dto.OrderRequest;
import com.luandeoliveira.order_service.core.producer.SagaProducer;
import com.luandeoliveira.order_service.core.repository.EventRepository;
import com.luandeoliveira.order_service.core.repository.OrderRepository;
import com.luandeoliveira.order_service.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EventService eventService;
    private final SagaProducer sagaProducer;
    private final JsonUtil jsonUtil;

    private static final String TRANSACATION_ID_PATTERN = "%s_%s";

    public Order createOrder(OrderRequest orderRequest){
        var order = Order
                .builder()
                .products(orderRequest.getProducts())
                .createdAt(LocalDateTime.now())
                .transactionId(String.format(TRANSACATION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID()))
                .build();

        orderRepository.save(order);
        sagaProducer.sendEvent(jsonUtil.toJson(createPayload(order)));
        return order;
    }

    public Event createPayload(Order order){
        var event = Event
                .builder()
                .orderId(order.getId())
                .createdAt(LocalDateTime.now())
                .payload(order)
                .transactionId(order.getTransactionId())
                .build();
        eventService.save(event);
        return event;
    }

}