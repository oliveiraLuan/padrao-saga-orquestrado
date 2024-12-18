package com.luandeoliveira.payment_service.core.service;

import com.luandeoliveira.payment_service.core.dto.Event;
import com.luandeoliveira.payment_service.core.dto.History;
import com.luandeoliveira.payment_service.core.dto.OrderProducts;
import com.luandeoliveira.payment_service.core.enums.EPaymentStatus;
import com.luandeoliveira.payment_service.core.enums.EventSource;
import com.luandeoliveira.payment_service.core.enums.SagaStatus;
import com.luandeoliveira.payment_service.core.model.Payment;
import com.luandeoliveira.payment_service.core.producer.KafkaProducer;
import com.luandeoliveira.payment_service.core.repository.PaymentRepository;
import com.luandeoliveira.payment_service.core.utils.JsonUtil;
import com.luandeoliveira.payment_service.exceptions.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private static final String CURRENT_SOURCE = "PAYMENT-SERVICE";
    private final Double REDUCE_SUM_VALUE = 0.0;
    private final Double MIN_AMOUNT_VALUE = 0.1;

    private final JsonUtil jsonUtil;
    private final KafkaProducer kafkaProducer;

    public void realizePayment(Event event){
        try {
            checkValidation(event);
            createPendingPayment(event);
            var payment = findByOrderIdAndTransactionId(event);
            validatePayment(payment);
            handleSuccess(event);
        }catch (Exception e){
            log.error("Erro ao tentar realizar pagamento.", e);
            handleFail(event, e.getMessage());
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    public void realizeRefund(Event event){
        event.setStatus(SagaStatus.FAIL);
        event.setSource(EventSource.PAYMENT_SERVICE);
        try {
            changeStatusToRefund(event);
            addHistory(event,"Rollback executado para o pagamento!");
        } catch (Exception ex){
            changeStatusToRefund(event);
            addHistory(event,"Rollback não executado para o pagamento!".concat(ex.getMessage()));
        }
        kafkaProducer.sendEvent(jsonUtil.toJson(event));
    }

    private void changeStatusToRefund(Event event){
       var payment = findByOrderIdAndTransactionId(event);
       payment.setStatus(EPaymentStatus.REFUND);
       setEventAmountItems(event, payment);
       save(payment);
    }

    public void handleSuccess(Event event){
        event.setSource(EventSource.PAYMENT_SERVICE);
        event.setStatus(SagaStatus.SUCCESS);
        addHistory(event, "Produtos validados com sucesso!");
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

    private void checkValidation(Event event){
        if(paymentRepository.existsByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId()))
            throw new ValidationException("Erro de validação do pagamento, já existe pagamento com este transactionId.");
    }
    private void createPendingPayment(Event event){
        var totalItems = calculateTotalItems(event);
        var totalAmount = calculateTotalAmount(event);

        var payment = Payment.builder()
                .orderId(event.getPayload().getId())
                .transactionId(event.getTransactionId())
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();
        save(payment);
        setEventAmountItems(event, payment);
    }

    private Payment findByOrderIdAndTransactionId(Event event){
        return paymentRepository
                .findByOrderIdAndTransactionId(event.getPayload().getId(), event.getTransactionId())
                        .orElseThrow(() ->  new ValidationException("Pagamento com orderId e transactionId informado não encontrado."));
    }

    public void validatePayment(Payment payment){
        if(payment.getTotalAmount() < MIN_AMOUNT_VALUE)
            throw new ValidationException("Valor do pagamento abaixo do mínimo de".concat(MIN_AMOUNT_VALUE.toString()));
    }

    private void save(Payment payment){
        paymentRepository.save(payment);
    }

    private void setEventAmountItems(Event event, Payment payment){
        event.getPayload().setTotalAmount(payment.getTotalAmount());
        event.getPayload().setTotalItems(payment.getTotalItems());
    }

    private Double calculateTotalAmount(Event event) {
        return event
                .getPayload()
                .getProducts()
                .stream()
                .map(product -> product.getQuantity() * product.getProduct().unitValue())
                .reduce(REDUCE_SUM_VALUE, Double::sum);
    }

    private Integer calculateTotalItems(Event event) {
        return event
                .getPayload()
                .getProducts()
                .stream()
                .map(OrderProducts::getQuantity)
                .reduce(REDUCE_SUM_VALUE.intValue(), Integer::sum);
    }
    private void handleFail(Event event, String message){
        event.setSource(EventSource.PAYMENT_SERVICE);
        event.setStatus(SagaStatus.ROLLBACK_PENDING);
        addHistory(event, "Falha ao validar pagamento! ".concat(message));
    }
}
