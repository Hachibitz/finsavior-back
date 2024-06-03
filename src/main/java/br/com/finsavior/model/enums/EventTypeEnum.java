package br.com.finsavior.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum EventTypeEnum {
    BILLING_SUBSCRIPTION_ACTIVATED("BILLING.SUBSCRIPTION.ACTIVATED"),
    BILLING_SUBSCRIPTION_CANCELLED("BILLING.SUBSCRIPTION.CANCELLED"),
    BILLING_SUBSCRIPTION_CREATED("BILLING.SUBSCRIPTION.CREATED"),
    BILLING_SUBSCRIPTION_EXPIRED("BILLING.SUBSCRIPTION.EXPIRED"),
    BILLING_SUBSCRIPTION_PAYMENT_FAILED("BILLING.SUBSCRIPTION.PAYMENT.FAILED"),
    BILLING_SUBSCRIPTION_SUSPENDED("BILLING.SUBSCRIPTION.SUSPENDED");


    private final String value;

    EventTypeEnum(String value) {this.value = value;}

    @JsonValue
    private String getValue(){
        return this.value;
    }

    public static EventTypeEnum valueOf(EventTypeEnum eventTypeEnum){
        return valueOf(eventTypeEnum.value);
    }
}
