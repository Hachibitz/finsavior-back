package br.com.finsavior.model.dto;

import br.com.finsavior.grpc.maintable.MainTableData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainTableDataResponseDTO {
    private List<MainTableData> mainTableDataList;
}
