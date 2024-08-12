package com.study.dawheen.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

// TODO 프론트 작업 완료 시에는 주석 풀 것

@Data
@AllArgsConstructor
public class UserCreateRequestDto {
    @Email
    String email;
    @NotNull
    String password;
    @NotNull
    String name;
//    String phone;
//    AddressDto address;
}
