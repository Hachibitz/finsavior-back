package br.com.finsavior.model.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    String newPassword;
    String token;
}
