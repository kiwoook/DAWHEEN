package com.study.dawheen.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    ADMIN("ADMIN"),
    ORGANIZATION("ORGANIZATION"),
    MEMBER("MEMBER"),
    GUEST("GUEST");

    private final String code;

    public static RoleType of(String code){
        return Arrays.stream(RoleType.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(GUEST);
    }
}
