package com.study.dawheen.user.dto;

import com.study.dawheen.common.dto.AddressDto;
import com.study.dawheen.auth.entity.ProviderType;
import com.study.dawheen.user.entity.User;
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
