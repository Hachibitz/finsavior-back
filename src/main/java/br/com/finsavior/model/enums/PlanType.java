package br.com.finsavior.model.enums;

import lombok.Getter;

@Getter
public enum PlanType {
    FREE(1L),
    PLUS(2L),
    PREMIUM(3L);

    private final Long planTypeId;

    PlanType(Long planTypeId) {
        this.planTypeId = planTypeId;
    }

    public static PlanType fromValue(Long value) {
        for (PlanType type : PlanType.values()) {
            if (type.getPlanTypeId() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
