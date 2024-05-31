package br.com.finsavior.model.dto;

import lombok.Data;

@Data
public class Payer {
    private Name name;
    private String externalEmailAddress;
    private String payerId;
    private Address address;
}
