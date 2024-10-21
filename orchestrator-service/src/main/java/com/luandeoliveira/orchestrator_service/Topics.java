package com.luandeoliveira.orchestrator_service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topics {
    NOTIFY_ENDING("notify-ending"),
    START_SAGA("start-saga"),
    BASE_ORCHESTRATOR("orchestrator"),
    FINISH_SUCCESS("finish-success"),
    FINISH_FAIL("finish-fail"),

    PRODUCT_VALIDATION_SUCCESS("product-validation-success"),
    PRODUCT_VALIDATION_FAIL("product-validation-fail"),

    PAYMENT_SUCCESS("payment-sucess"),
    PAYMENT_FAIL("payment-fail"),

    INVENTORY_SUCCESS("inventory-success"),
    INVENTORY_FAIL("inventory_fail");


    private String topic;
}
