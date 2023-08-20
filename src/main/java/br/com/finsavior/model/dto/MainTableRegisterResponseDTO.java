package br.com.finsavior.model.dto;

import br.com.finsavior.grpc.maintable.MainTableRequestDTO;

public class MainTableRegisterResponseDTO {
    private String status;
    private MainTableRequestDTO message;

    public MainTableRegisterResponseDTO() {
    }

    public MainTableRegisterResponseDTO(String status, MainTableRequestDTO message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MainTableRequestDTO getMessage() {
        return message;
    }

    public void setMessage(MainTableRequestDTO message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return "Dados salvos\n______________\n"+this.message.toString();
    }
}
