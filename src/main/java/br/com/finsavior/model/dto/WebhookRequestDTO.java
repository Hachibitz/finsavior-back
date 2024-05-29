package br.com.finsavior.model.dto;

import br.com.finsavior.model.enums.EventTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebhookRequestDTO {

    private String id;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("event_time")
    private EventTypeEnum event_type;

    private String summary;

    private ResourceDTO resource;
}
