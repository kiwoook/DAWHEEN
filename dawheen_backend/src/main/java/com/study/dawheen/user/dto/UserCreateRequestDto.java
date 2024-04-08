package com.study.dawheen.user.dto;

import com.study.dawheen.common.dto.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserCreateRequestDto {
    @Email
    String email;
    String password;
    String name;
    String phone;
    @Valid
    AddressDto address;
}
