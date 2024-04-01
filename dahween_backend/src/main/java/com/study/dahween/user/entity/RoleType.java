package com.study.dahween.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    ADMIN("ROLE_ADMIN"),
    ORGANIZATION("ROLE_ORGANIZATION"),
    MEMBER("ROLE_MEMBER"),
    GUEST("ROLE_GUEST");

    private final String code;

    public static RoleType of(String code){
        return Arrays.stream(RoleType.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(GUEST);
    }
}
