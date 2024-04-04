package com.study.dahween.user.dto;

import com.study.dahween.common.dto.AddressDto;
import lombok.Data;

@Data
public class OAuth2UserCreateRequestDto {
    private String email;
    private String name;
    private String phone;
    private String birt;
    private AddressDto address;
}
