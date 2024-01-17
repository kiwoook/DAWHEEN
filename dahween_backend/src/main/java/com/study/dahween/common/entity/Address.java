package com.study.dahween.common.entity;

import com.study.dahween.common.dto.AddressDto;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    private String roadAddress;
    private String detailAddress;
    private String zipCode;

    @Builder
    public Address(String roadAddress, String detailAddress, String zipCode) {
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
    }

    public static Address toEntity(AddressDto addressDto){
        return Address.builder()
                .roadAddress(addressDto.getRoadAddress())
                .detailAddress(addressDto.getDetailAddress())
                .zipCode(addressDto.getZipCode())
                .build();
    }
}
