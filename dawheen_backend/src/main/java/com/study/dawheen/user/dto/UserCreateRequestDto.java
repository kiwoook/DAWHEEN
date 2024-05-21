package com.study.dawheen.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

// TODO 프론트 작업 완료 시에는 주석 풀 것

@Data
public class UserCreateRequestDto {
    @Email
    String email;
    String password;
    String name;
//    String phone;
//    @Valid
//    AddressDto address;
}
