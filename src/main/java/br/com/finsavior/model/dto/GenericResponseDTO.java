package br.com.finsavior.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class GenericResponseDTO {

    private String status;
    private String message;
}