package com.study.dawheen.organization.dto;

import com.study.dawheen.common.dto.AddressDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link com.study.dawheen.organization.entity.Organization}
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganRequestDto {

    @NotBlank
    private String name;
    @NotBlank
    private String facilityPhone;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String facilityType;

    private String representName;

    @NotNull
    private AddressDto address;
}