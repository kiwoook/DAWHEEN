package com.study.dahween.user.dto;

import com.study.dahween.common.dto.AddressDto;
import lombok.Data;

@Data
public class OAuth2UserCreateRequestDto {

    private String name;
    private String phone;
    private AddressDto address;
}
