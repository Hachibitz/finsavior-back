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
    private ArrayList<PurchaseUnit> purchaseUnits;
    private Payer payer;
    private LocalDateTime createTime;
}
