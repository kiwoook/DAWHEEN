package com.study.dawheen.user.dto;

import com.study.dawheen.auth.entity.ProviderType;
import com.study.dawheen.common.dto.AddressDto;
import com.study.dawheen.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoResponseDto {

    private String name;

    @Email
    private String email;

    private String phone;

    @Valid
    private AddressDto address;

    private ProviderType providerType;


    public UserInfoResponseDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.address = user.getAddress() != null ? new AddressDto(user.getAddress()) : null;
        this.providerType = user.getProviderType();
    }
}
