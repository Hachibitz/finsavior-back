package br.com.finsavior.model.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    ROLE_USER(1L),
    ROLE_ADMIN(2L);

    public final Long id;

    UserRoleEnum (Long id) {
        this.id = id;
    }
}