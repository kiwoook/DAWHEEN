package com.study.dahween.user.dto;

import com.study.dahween.common.dto.AddressDto;
import lombok.Data;

@Data
public class UserCreateRequestDto {

    String email;
    String password;
    String name;
    String phone;
    AddressDto address;
}
