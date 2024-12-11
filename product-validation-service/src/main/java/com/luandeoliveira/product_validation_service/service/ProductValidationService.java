package com.luandeoliveira.product_validation_service.service;

import com.luandeoliveira.product_validation_service.core.dto.Event;
import com.luandeoliveira.product_validation_service.core.dto.History;
import com.luandeoliveira.product_validation_service.core.enums.EventSource;
import com.luandeoliveira.product_validation_service.core.enums.SagaStatus;
import com.luandeoliveira.product_validation_service.core.model.Validation;
import com.luandeoliveira.product_validation_service.core.producer.KafkaProducer;
import com.luandeoliveira.product_validation_service.core.repository.ProductRepository;
import com.luandeoliveira.product_validation_service.core.repository.ValidationRepository;
import com.luandeoliveira.product_validation_service.core.utils.JsonUtil;
import com.luandeoliveira.product_validation_service.exceptions.ValidationException;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@AllArgsConstructor
@Service
public class ProductValidationService {
    private static final String CURRENT_SOURCE = "PRODUCT_VALIDATION_SERVICE";

    private final JsonUtil jsonUtil;

    private final KafkaProducer kafkaProducer;

    private final ProductRepository productRepository;

    private final ValidationRepository validationRepository;

    public void validateExistingProducts(Event event){
        try {
            checkCurrentValidation(event);
            createValidation(event, true);
            handleSuccess(event);
        } catch (Exception e){
          log.error("Falha ao validar o evento", e);
          handleFail(event, e.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void validateProductsInformed(Event event){
        if(isEmpty(event.getPayload()) || isEmpty(event.getPayload().getProducts())){
            throw new ValidationException("Lista de produtos vazia.");
        }
        if(isEmpty(event.getPayload().getId()) || isEmpty(event.getTransactionId())){
            throw new ValidationException("OrderId e TransactionId precisam ser informados.");
        }
    }

    private void checkCurrentValidation(Event event){
        validateProductsInformed(event);
        if(validationRepository.existsByOrderByOrderIdAndTransactionId(event.getOrderId(), event.getTransactionId())){
            throw new ValidationException("Já existe outra validação com este OrderId e TransactionId");
        }
        event.getPayload().getProducts().forEach(product -> {
            if(StringUtils.isEmpty(product.getId())){
                throw new ValidationException("Id do produto precisa ser informado");
            }
            validateExistingProduct(product.getId());
        });
    }

    private void validateExistingProduct(String code){
        if(productRepository.existsByCode(code)){
            throw new ValidationException("Não existe produto com id informado");
        }
    }

    private void createValidation(Event event, Boolean success){
        var validation = Validation
                .builder()
                .createdAt(LocalDateTime.now())
                .orderId(event.getId())
                .transactionId(event.getTransactionId())
                .success(success)
                .build();
        validationRepository.save(validation);
    }
    public void handleSuccess(Event event){
        changeValidationToFail(event);
        event.setSource(EventSource.PRODUCT_VALIDATION_SERVICE);
        event.setStatus(SagaStatus.SUCCESS);
        addHistory(event, "Produtos validados com sucesso!");
    }

    private void handleFail(Event event, String message){
        event.setSource(EventSource.PRODUCT_VALIDATION_SERVICE);
        event.setStatus(SagaStatus.ROLLBACK_PENDING);
        addHistory(event, "Falha ao validar produtos! ".concat(message));
    }

    private void changeValidationToFail(Event event){
        validationRepository.findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                .ifPresentOrElse(
                        validation -> {
                            validation.setSuccess(false);
                            validationRepository.save(validation)
                        },
                        () -> createValidation(event, false)
                );

    }

    private void rollbackEvent(Event event){
        event.setSource(EventSource.PRODUCT_VALIDATION_SERVICE);
        event.setStatus(SagaStatus.FAIL);
        addHistory(event, "Rollback na validação de produto.");
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
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
}
