package br.com.finsavior.model.dto;

import lombok.Data;

@Data
public class PurchaseAmount {
    private String currencyCode;
    private Double value;
}
