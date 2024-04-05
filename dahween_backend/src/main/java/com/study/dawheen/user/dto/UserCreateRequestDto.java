package com.study.dawheen.user.dto;

import com.study.dawheen.common.dto.AddressDto;
import lombok.Data;

@Data
public class UserCreateRequestDto {

    String email;
    String password;
    String name;
    String phone;
    AddressDto address;
}
