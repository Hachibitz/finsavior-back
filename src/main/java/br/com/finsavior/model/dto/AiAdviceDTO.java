package br.com.finsavior.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAdviceDTO {
    private Integer analysisTypeId;
    private String mainAndIncomeTable;
    private String cardTable;
    private String date;
    private Double temperature;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
}
