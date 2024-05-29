package br.com.finsavior.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubscriberDTO {

    private NameDTO name;

    @JsonProperty("email_address")
    private String email;

    @Data
    public static class NameDTO {
        private String given_name;
        private String surname;
    }
}
