package com.study.dahween.organization.entity;


import com.study.dahween.common.dto.CoordinateDto;
import com.study.dahween.common.entity.Address;
import com.study.dahween.common.entity.BaseTimeEntity;
import com.study.dahween.common.entity.Coordinate;
import com.study.dahween.organization.dto.OrganRequestDto;
import com.study.dahween.user.entity.User;
import com.study.dahween.volunteer.entity.VolunteerWork;
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

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<User> users = new ArrayList<>();
    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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
    @Embedded
    @Column(nullable = false)
    private Address address;
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "COORDINATE_ID")
    private Coordinate coordinate;
    private Boolean approved;

    @Builder
    public Organization(String name, String facilityPhone, String email, String facilityType, String representName, Address address) {
        this.name = name;
        this.facilityPhone = facilityPhone;
        this.email = email;
        this.facilityType = facilityType;
        this.representName = representName;
        this.address = address;
        this.approved = false;
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
        return Objects.equals(getUsers(), that.getUsers()) && Objects.equals(getWorkList(), that.getWorkList()) && Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getFacilityPhone(), that.getFacilityPhone()) && Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getFacilityType(), that.getFacilityType()) && Objects.equals(getRepresentName(), that.getRepresentName()) && Objects.equals(getAddress(), that.getAddress()) && Objects.equals(getCoordinate(), that.getCoordinate()) && Objects.equals(getApproved(), that.getApproved());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsers(), getWorkList(), getId(), getName(), getFacilityPhone(), getEmail(), getFacilityType(), getRepresentName(), getAddress(), getCoordinate(), getApproved());
    }

    public void update(OrganRequestDto requestDto) {
        this.name = requestDto.getName();
        this.facilityType = requestDto.getFacilityType();
        this.email = requestDto.getEmail();
        this.facilityPhone = requestDto.getFacilityPhone();
        this.representName = requestDto.getRepresentName();
        this.address = Address.toEntity(requestDto.getAddress());
    }

    public void updateCoordinate(CoordinateDto coordinateDto) {
        this.coordinate = new Coordinate(coordinateDto.getLatitude(), coordinateDto.getLongitude());
    }

    public void approved() {
        this.approved = true;
    }

    public void addUser(User user) {
        this.users.add(user);
        user.grantOrganization(this);
    }

    public void revokeUser(User user) {
        this.users.remove(user);
    }


}
