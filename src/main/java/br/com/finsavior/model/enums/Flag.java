package br.com.finsavior.model.enums;

import lombok.Getter;

@Getter
public enum Flag {
    S("Sim"),
    N("Não");

    private String value;

    Flag(String value) {
        this.value = value;
    }
}
