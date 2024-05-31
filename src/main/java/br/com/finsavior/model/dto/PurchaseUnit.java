package br.com.finsavior.model.dto;

import lombok.Data;

@Data
public class PurchaseUnit {
    private String referenceId;
    private PurchaseAmount amount;
    private Payee payee;
    private Shipping shipping;
}
