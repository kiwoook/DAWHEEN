package com.study.dahween.user.dto;

import com.study.dahween.common.dto.AddressDto;
import com.study.dahween.auth.entity.ProviderType;
import com.study.dahween.user.entity.User;
import lombok.Data;

@Data
public class UserInfoResponseDto {
    private String name;

    private String email;

    private String phone;

    private AddressDto address;

    private ProviderType providerType;


    public UserInfoResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        if (user.getAddress() != null) {
            this.address = new AddressDto(user.getAddress());
        }
        this.providerType = user.getProviderType();
    }
}
