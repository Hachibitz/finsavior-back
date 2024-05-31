package br.com.finsavior.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalUserDTO {
    private Long userId;
    private String email;
    private String name;
    private String planId;
    private String planStatus;
    private String subscriptionId;
    private String externalUserId;
}
