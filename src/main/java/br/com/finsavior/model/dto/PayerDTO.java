package br.com.finsavior.model.dto;

import lombok.Data;

@Data
public class PayerDTO {
    private NameDTO name;
    private String externalEmailAddress;
    private String payerId;
    private AddressDTO address;
}
