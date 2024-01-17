package com.study.dahween.organization.entity;


import com.study.dahween.common.entity.Address;
import com.study.dahween.common.entity.BaseTimeEntity;
import com.study.dahween.organization.dto.OrganCreateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Organization extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "FACILITY", nullable = false)
    private String facilityPhone;

    @Column(nullable = false)
    private String email;

    @Column(name = "FACILITY_TYPE", nullable = false)
    private String facilityType;

    @Column(name = "REPRESENT_NAME")
    private String representName;

    @Embedded
    @Column(nullable = false)
    private Address address;

    @Builder
    public Organization(String name, String facilityPhone, String email, String facilityType, String representName, Address address) {
        this.name = name;
        this.facilityPhone = facilityPhone;
        this.email = email;
        this.facilityType = facilityType;
        this.representName = representName;
        this.address = address;
    }

    public static Organization toEntity(OrganCreateRequestDto requestDto) {
        return Organization.builder()
                .name(requestDto.getName())
                .address(Address.toEntity(requestDto.getAddress()))
                .email(requestDto.getEmail())
                .facilityType(requestDto.getFacilityType())
                .facilityPhone(requestDto.getFacilityPhone())
                .representName(requestDto.getRepresentName())
                .build();
    }
}
