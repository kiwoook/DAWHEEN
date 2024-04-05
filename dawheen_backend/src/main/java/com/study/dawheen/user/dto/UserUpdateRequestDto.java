package com.study.dawheen.user.dto;

import com.study.dawheen.common.dto.AddressDto;
import lombok.Data;

@Data
public class UserUpdateRequestDto {
    String name;
    String email;
    String phone;
    AddressDto address;
}
