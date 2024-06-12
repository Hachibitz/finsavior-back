package br.com.finsavior.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiAnalysisResponseDTO {
    private Long id;
    private Integer analysisType;
    private String resultAnalysis;
    private String date;
    private String startDate;
    private String finishDate;
    private Float temperature;
}
