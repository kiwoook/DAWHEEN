package com.study.dawheen.organization.dto;

import com.study.dawheen.common.dto.AddressDto;
import com.study.dawheen.common.dto.CoordinateInfoResponseDto;
import com.study.dawheen.organization.entity.Organization;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * DTO for {@link com.study.dawheen.organization.entity.Organization}
 */

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganInfoResponseDto {
    private String name;
    private String facilityPhone;
    @Email
    private String email;
    private String facilityType;
    private String representName;
    private AddressDto address;

    private CoordinateInfoResponseDto coordinateInfoResponseDto;

    public OrganInfoResponseDto(Organization organization) {
        this.name = organization.getName();
        this.facilityPhone = organization.getFacilityPhone();
        this.email = organization.getEmail();
        this.facilityType = organization.getFacilityType();
        this.representName = organization.getRepresentName();
        this.address = Optional.ofNullable(organization.getAddress())
                .map(AddressDto::new)
                .orElse(null);
        this.coordinateInfoResponseDto = Optional.ofNullable(organization.getCoordinate())
                .map(CoordinateInfoResponseDto::new)
                .orElse(null);
    }
}