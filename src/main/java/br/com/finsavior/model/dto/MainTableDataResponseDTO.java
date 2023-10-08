package br.com.finsavior.model.dto;

import br.com.finsavior.grpc.maintable.MainTableData;

import java.util.List;

public class MainTableDataResponseDTO {
    private List<MainTableData> mainTableDataList;

    public MainTableDataResponseDTO() {

    }

    public MainTableDataResponseDTO(List<MainTableData> mainTableDataList) {
        this.mainTableDataList = mainTableDataList;
    }

    public List<MainTableData> getMainTableDataList() {
        return mainTableDataList;
    }

    public void setMainTableDataList(List<MainTableData> mainTableDataList) {
        this.mainTableDataList = mainTableDataList;
    }
}
