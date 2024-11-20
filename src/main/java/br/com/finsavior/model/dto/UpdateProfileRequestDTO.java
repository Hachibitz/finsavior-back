package br.com.finsavior.model.dto;

import lombok.Data;

@Data
public class UpdateProfileRequestDTO {
    private String firstName = null;
    private String lastName = null;
}
