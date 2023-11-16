package br.com.finsavior.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainTableDataDTO {
    private Long id;
    private String billType;
    private String billDate;
    private String billName;
    private Double billValue;
    private String billDescription;
    private boolean isPaid;
}
