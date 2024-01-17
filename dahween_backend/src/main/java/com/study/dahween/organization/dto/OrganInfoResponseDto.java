package com.study.dahween.organization.dto;

import com.study.dahween.common.dto.AddressDto;
import com.study.dahween.common.entity.Address;
import com.study.dahween.organization.entity.Organization;
import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * DTO for {@link com.study.dahween.organization.entity.Organization}
 */

@Data
public class OrganInfoResponseDto {
    private String name;
    private String facilityPhone;
    @Email
    private String email;
    private String facilityType;
    private String representName;
    private AddressDto address;

    public OrganInfoResponseDto(Organization organization) {
        this.name = organization.getName();
        this.facilityPhone = organization.getFacilityPhone();
        this.email = organization.getEmail();
        this.facilityType = organization.getFacilityType();
        this.representName = organization.getRepresentName();
        this.address = new AddressDto(organization.getAddress());
    }
}