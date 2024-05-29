package br.com.finsavior.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventTypeEnum {
    BILLING_SUBSCRIPTION_ACTIVATED("BILLING.SUBSCRIPTION.ACTIVATED"),
    BILLING_SUBSCRIPTION_CANCELLED("BILLING.SUBSCRIPTION.CANCELLED"),
    BILLING_SUBSCRIPTION_CREATED("BILLING.SUBSCRIPTION.CREATED"),
    BILLING_SUBSCRIPTION_EXPIRED("BILLING.SUBSCRIPTION.EXPIRED"),
    BILLING_SUBSCRIPTION_PAYMENT_FAILED("BILLING.SUBSCRIPTION.PAYMENT.FAILED"),
    BILLING_SUBSCRIPTION_SUSPENDED("BILLING.SUBSCRIPTION.SUSPENDED");


    private String name;

    EventTypeEnum(String name) {this.name = name;}

    @JsonValue
    private String getName(){
        return this.name;
    }

    public static EventTypeEnum valueOf(EventTypeEnum eventTypeEnum){
        return valueOf(eventTypeEnum.name);
    }
}
