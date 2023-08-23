package br.com.finsavior.model.dto;

import br.com.finsavior.grpc.maintable.BillRegisterRequest;

public class BillRegisterResponseDTO {
    private String status;
    private BillRegisterRequest message;

    public BillRegisterResponseDTO() {
    }

    public BillRegisterResponseDTO(String status, BillRegisterRequest message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BillRegisterRequest getMessage() {
        return message;
    }

    public void setMessage(BillRegisterRequest message) {
        this.message = message;
    }

    @Override
    public String toString(){
        return "Dados salvos\n______________\n"+this.message.toString();
    }
}
