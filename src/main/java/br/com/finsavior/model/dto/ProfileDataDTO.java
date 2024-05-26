package br.com.finsavior.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDataDTO {
    private String username;
    private String profilePicture;
    private String email;
    private String plan;
}
