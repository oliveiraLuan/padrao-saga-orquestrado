package com.luandeoliveira.product_validation_service.service;

import com.luandeoliveira.product_validation_service.core.dto.Event;
import com.luandeoliveira.product_validation_service.core.dto.OrderProducts;
import com.luandeoliveira.product_validation_service.core.producer.KafkaProducer;
import com.luandeoliveira.product_validation_service.core.repository.ProductRepository;
import com.luandeoliveira.product_validation_service.core.repository.ValidationRepository;
import com.luandeoliveira.product_validation_service.core.utils.JsonUtil;
import com.luandeoliveira.product_validation_service.exceptions.ValidationException;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        } catch (Exception e){
          log.error("Falha ao validar o evento", e.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void validateProductsInformed(Event event){
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
}
