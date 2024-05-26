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
}
