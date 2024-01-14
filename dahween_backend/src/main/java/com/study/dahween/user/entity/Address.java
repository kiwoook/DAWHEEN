package com.study.dahween.user.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    private String roadAddress;
    private String detailAddress;
    private String zipCode;

    public Address(String roadAddress, String detailAddress, String zipCode) {
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
    }
}
