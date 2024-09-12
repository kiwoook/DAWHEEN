package com.study.dawheen.organization.dto;

import com.study.dawheen.common.dto.AddressDto;
import com.study.dawheen.common.dto.CoordinateDto;
import com.study.dawheen.organization.entity.Organization;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private CoordinateDto coordinateDto;

    public OrganInfoResponseDto(Organization organization) {
        this.name = organization.getName();
        this.facilityPhone = organization.getFacilityPhone();
        this.email = organization.getEmail();
        this.facilityType = organization.getFacilityType();
        this.representName = organization.getRepresentName();
        this.address = AddressDto.toDto(organization.getAddress());
        this.coordinateDto = CoordinateDto.toDto(organization.getCoordinate());
    }

    public static OrganInfoResponseDto toDto(Organization organization) {
        if (organization == null) {
            return null;
        }

        return new OrganInfoResponseDto(organization);
    }
}