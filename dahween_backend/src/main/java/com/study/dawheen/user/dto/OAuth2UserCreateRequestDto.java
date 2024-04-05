package com.study.dawheen.user.dto;

import com.study.dawheen.common.dto.AddressDto;
import lombok.Data;

@Data
public class OAuth2UserCreateRequestDto {
    private String email;
    private String name;
    private String phone;
    private String birt;
    private AddressDto address;
}
