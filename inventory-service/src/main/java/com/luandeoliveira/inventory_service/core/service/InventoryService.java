package com.luandeoliveira.inventory_service.core.service;

import com.luandeoliveira.inventory_service.core.dto.Event;
import com.luandeoliveira.inventory_service.core.dto.OrderProducts;
import com.luandeoliveira.inventory_service.core.model.Inventory;
import com.luandeoliveira.inventory_service.core.model.OrderInventory;
import com.luandeoliveira.inventory_service.core.producer.KafkaProducer;
import com.luandeoliveira.inventory_service.core.repository.InventoryRepository;
import com.luandeoliveira.inventory_service.core.repository.OrderInventoryRepository;
import com.luandeoliveira.inventory_service.core.utils.JsonUtil;
import com.luandeoliveira.inventory_service.exceptions.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class InventoryService {
    private final OrderInventoryRepository orderInventoryRepository;
    private final InventoryRepository inventoryRepository;
    private final KafkaProducer kafkaProducer;
    private final JsonUtil jsonUtil;

    public void updateInventory(Event event){
        try {
            checkCurrentValidation(event);
            createOrderInventory(event);
        } catch (Exception e){
            log.error("Erro ao tentar atualizar inventário", e);
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void createOrderInventory(Event event) {
        event.getPayload()
                .getProducts()
                .forEach(orderProduct -> {
                    var inventory = findInventoryByProductCode(orderProduct.getProduct().code());
                    var orderInventory = createOrderInventory(event, orderProduct, inventory);
                    orderInventoryRepository.save(orderInventory);
                });
    }

    private OrderInventory createOrderInventory(Event event, OrderProducts orderProduct, Inventory inventory) {
        return OrderInventory.builder()
                .inventory(inventory)
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .oldQuantity(inventory.getAvailable())
                .orderQuantity(orderProduct.getQuantity())
                .newQuantity(inventory.getAvailable() - orderProduct.getQuantity())
                .build();
    }

    private Inventory findInventoryByProductCode(String productCode){
        return inventoryRepository.findByProductCode(productCode)
                .orElseThrow(() -> new ValidationException("Não foi encontrado inventory com productCode informado."));
    }

    private void checkCurrentValidation(Event event) {
        if(orderInventoryRepository.existsByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId()))
            throw new ValidationException("OrderInventory não encontrado com OrderId e TransactionId informado.");
    }
}
