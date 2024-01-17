package com.study.dahween.organization.dto;

import com.study.dahween.common.dto.AddressDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for {@link com.study.dahween.organization.entity.Organization}
 */
@Data
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