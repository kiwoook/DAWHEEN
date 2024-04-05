package com.study.dawheen.common.entity;

import com.study.dawheen.common.dto.AddressDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String roadAddress;
    private String detailAddress;
    private String zipCode;

    @Builder
    public Address(String roadAddress, String detailAddress, String zipCode) {
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
    }

    public static Address toEntity(AddressDto addressDto) {
        return Address.builder()
                .roadAddress(addressDto.getRoadAddress())
                .detailAddress(addressDto.getDetailAddress())
                .zipCode(addressDto.getZipCode())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(roadAddress, address.roadAddress) && Objects.equals(detailAddress, address.detailAddress) && Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roadAddress, detailAddress, zipCode);
    }
}
