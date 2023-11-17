package br.com.finsavior.model.dto;

import lombok.Data;

@Data
public class ChangePasswordRequestDTO {
    private String username;
    private String currentPassword;
    private String newPassword;
}
