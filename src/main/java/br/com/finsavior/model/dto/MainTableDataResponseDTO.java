package br.com.finsavior.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainTableDataResponseDTO {
    private List<MainTableDataDTO> mainTableDataList;
}
