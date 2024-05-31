package br.com.finsavior.model.dto;

import lombok.Data;

@Data
public class PurchaseUnitDTO {
    private String referenceId;
    private PurchaseAmount amount;
    private PayeeDTO payee;
    private ShippingDTO shipping;
}
