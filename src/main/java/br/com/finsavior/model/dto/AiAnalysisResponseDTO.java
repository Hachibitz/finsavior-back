package br.com.finsavior.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiAnalysisResponseDTO {
    private Long id;
    private Integer analysisType;
    private String resultAnalysis;
    private String date;
    private String startDate;
    private String finishDate;
    private Float temperature;
}
