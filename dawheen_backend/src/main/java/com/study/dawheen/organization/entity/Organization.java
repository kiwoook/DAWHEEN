package com.study.dawheen.organization.entity;


import com.study.dawheen.common.dto.AddressDto;
import com.study.dawheen.common.entity.Address;
import com.study.dawheen.common.entity.BaseTimeEntity;
import com.study.dawheen.common.entity.Coordinate;
import com.study.dawheen.organization.dto.OrganRequestDto;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Organization extends BaseTimeEntity {

    // 기관과 관련된 유저들
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    List<VolunteerWork> workList = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
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

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Address address;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "COORDINATE_ID")
    private Coordinate coordinate;

    private Boolean approved;

    @Builder
    public Organization(String name, String facilityPhone, String email, String facilityType, String representName, Address address, Coordinate coordinate) {
        this.name = name;
        this.facilityPhone = facilityPhone;
        this.email = email;
        this.facilityType = facilityType;
        this.representName = representName;
        this.address = address;
        this.approved = false;
        this.coordinate = coordinate;
    }

    public static Organization toEntity(OrganRequestDto requestDto) {
        return Organization.builder()
                .name(requestDto.getName())
                .address(Address.toEntity(requestDto.getAddress()))
                .email(requestDto.getEmail())
                .facilityType(requestDto.getFacilityType())
                .facilityPhone(requestDto.getFacilityPhone())
                .representName(requestDto.getRepresentName())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getFacilityPhone(), that.getFacilityPhone()) && Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getFacilityType(), that.getFacilityType()) && Objects.equals(getRepresentName(), that.getRepresentName()) && Objects.equals(getAddress(), that.getAddress()) && Objects.equals(getCoordinate(), that.getCoordinate()) && Objects.equals(getApproved(), that.getApproved());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getFacilityPhone(), getEmail(), getFacilityType(), getRepresentName(), getAddress(), getCoordinate(), getApproved());
    }

    public void update(String name, String facilityType, String email, String facilityPhone, String representName, AddressDto addressDto) {
        this.name = name;
        this.facilityType = facilityType;
        this.email = email;
        this.facilityPhone = facilityPhone;
        this.representName = representName;
        this.address = Address.toEntity(addressDto);
    }

    public void updateCoordinate(Double latitude, Double longitude) {
        this.coordinate = new Coordinate(latitude, longitude);
    }

    public void approved() {
        this.approved = true;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void revokeUser(User user) {
        this.users.remove(user);
    }

}
