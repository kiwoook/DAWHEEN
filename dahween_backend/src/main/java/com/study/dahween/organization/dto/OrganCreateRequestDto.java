package com.study.dahween.organization.dto;

import com.study.dahween.common.dto.AddressDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.study.dahween.organization.entity.Organization}
 */
@Data
public class OrganCreateRequestDto {
    @NotBlank
    String name;
    @NotBlank
    String facilityPhone;
    @Email
    @NotBlank
    String email;
    @NotBlank
    String facilityType;
    String representName;
    @NotNull
    AddressDto address;
}