package br.com.finsavior.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SignUpResponseDTO {

    private String status;
    private String message;
}