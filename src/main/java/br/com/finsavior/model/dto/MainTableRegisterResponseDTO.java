package br.com.finsavior.model.dto;

public class MainTableRegisterResponseDTO {
    private String status;
    private MainTableRegisterRequestDTO message;

    public MainTableRegisterResponseDTO() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MainTableRegisterRequestDTO getMessage() {
        return message;
    }

    public void setMessage(MainTableRegisterRequestDTO message) {
        this.message = message;
    }
}
