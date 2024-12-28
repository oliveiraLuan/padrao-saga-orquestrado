package com.luandeoliveira.inventory_service.core.service;

import com.luandeoliveira.inventory_service.core.dto.Event;
import com.luandeoliveira.inventory_service.core.dto.History;
import com.luandeoliveira.inventory_service.core.dto.Order;
import com.luandeoliveira.inventory_service.core.dto.OrderProducts;
import com.luandeoliveira.inventory_service.core.model.Inventory;
import com.luandeoliveira.inventory_service.core.model.OrderInventory;
import com.luandeoliveira.inventory_service.core.producer.KafkaProducer;
import com.luandeoliveira.inventory_service.core.repository.InventoryRepository;
import com.luandeoliveira.inventory_service.core.repository.OrderInventoryRepository;
import com.luandeoliveira.inventory_service.core.utils.JsonUtil;
import com.luandeoliveira.inventory_service.enums.EventSource;
import com.luandeoliveira.inventory_service.enums.SagaStatus;
import com.luandeoliveira.inventory_service.exceptions.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
            updateInventory(event.getPayload());
            handleSuccess(event);
        } catch (Exception e){
            log.error("Erro ao tentar atualizar inventário", e);
            handleFail(event, e.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    public void rollbackInventory(Event event){
        event.setStatus(SagaStatus.FAIL);
        event.setSource(EventSource.INVENTORY_SERVICE);
        try {
            rollbackInventoryToPreviousValues(event);
            addHistory(event,"Rollback executado para o inventário!");
        } catch (Exception ex){
            addHistory(event,"Rollback não executado para o inventário!".concat(ex.getMessage()));
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void rollbackInventoryToPreviousValues(Event event){
        orderInventoryRepository.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .forEach(
                        orderInventory -> {
                            var inventory = orderInventory.getInventory();
                            inventory.setAvailable(orderInventory.getOldQuantity());
                            inventoryRepository.save(inventory);
                            log.info("Inventário com orderId {} atualizado do valor {} para {}", event.getPayload().getId(), orderInventory.getNewQuantity(), inventory.getAvailable());
                        }
                );
    }

    private void handleFail(Event event, String message){
        event.setSource(EventSource.INVENTORY_SERVICE);
        event.setStatus(SagaStatus.ROLLBACK_PENDING);
        addHistory(event, "Falha ao atualizar inventário! ".concat(message));
    }

    private void updateInventory(Order order){
        order.getProducts().forEach(
                product -> {
                    var inventory = findInventoryByProductCode(product.getProduct().code());
                    checkInventory(inventory.getAvailable(), product.getQuantity());
                    inventory.setAvailable(inventory.getAvailable() - product.getQuantity());
                    inventoryRepository.save(inventory);
                }
        );
    }

    private void handleSuccess(Event event){
        event.setSource(EventSource.INVENTORY_SERVICE);
        event.setStatus(SagaStatus.SUCCESS);
        addHistory(event, "Inventário validado com sucesso!");
    }

    public void addHistory(Event event, String message){
        History history = History
                .builder()
                .createdAt(LocalDateTime.now())
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .build();
        event.addHistory(history);
    }

    private void checkInventory(int available, int orderQuantity) {
        if(available < orderQuantity)
            throw new ValidationException("Quantidade do pedido maior que o disponível em estoque.");
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
