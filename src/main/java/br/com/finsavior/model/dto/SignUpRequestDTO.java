package br.com.finsavior.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpRequestDTO {
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
}
