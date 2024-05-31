package br.com.finsavior.model.dto;

import lombok.Data;

@Data
public class Address {
    private String addressLine1;
    private String addressLine2;
    private String adminArea2;
    private String adminArea1;
    private String postalCode;
    private String countryCode;
}
