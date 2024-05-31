package br.com.finsavior.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class SubscriptionDTO {
    private String planId;
    private String externalUserId;
    private String subscriptionId;
    private String intent;
    private String status;
    private ArrayList<PurchaseUnitDTO> purchaseUnits;
    private PayerDTO payer;
    private LocalDateTime createTime;
}
