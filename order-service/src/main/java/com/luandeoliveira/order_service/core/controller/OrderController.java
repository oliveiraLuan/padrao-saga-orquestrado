package com.luandeoliveira.order_service.core.controller;

import com.luandeoliveira.order_service.core.dto.OrderRequest;
import com.luandeoliveira.order_service.core.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/order")
@RestController
@AllArgsConstructor
public class OrderController {
    private OrderService orderService;

    @PostMapping
    public void createOrder(@RequestBody OrderRequest orderRequest){
        orderService.createOrder(orderRequest);
    }
}
