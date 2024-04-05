package com.study.dawheen.user.dto;

import lombok.Data;

@Data
public class UserResetPasswordRequestDto {
    private String email;
    private String name;

}
