package br.com.finsavior.model.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum AnalysisType {
    MONTH(1, List.of(PlanType.FREE, PlanType.PLUS, PlanType.PREMIUM)),
    TRIMESTER(2, List.of(PlanType.PLUS, PlanType.PREMIUM)),
    ANNUAL(3, List.of(PlanType.PREMIUM));

    private final Integer analysisTypeId;
    private final List<PlanType> plansCoverageList;

    AnalysisType(Integer analysisTypeId, List<PlanType> plansCoverageList) {
        this.analysisTypeId = analysisTypeId;
        this.plansCoverageList = plansCoverageList;
    }
}
