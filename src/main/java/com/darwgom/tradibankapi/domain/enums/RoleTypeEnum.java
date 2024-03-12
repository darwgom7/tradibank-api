package com.darwgom.tradibankapi.domain.enums;

public enum RoleTypeEnum {
    ROLE_USER("role_user"),
    ROLE_ADMIN("role_admin");

    private final String value;

    RoleTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

