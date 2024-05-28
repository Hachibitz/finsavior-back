package br.com.finsavior.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResourceDTO {

    private String id;

    private String quantity;

    private SubscriberDTO subscriber;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("plan_id")
    private String planId;
}
