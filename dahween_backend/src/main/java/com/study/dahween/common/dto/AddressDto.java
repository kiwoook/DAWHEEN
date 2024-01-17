package com.study.dahween.common.dto;

import com.study.dahween.common.entity.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for {@link com.study.dahween.common.entity.Address}
 */
@Data
public class AddressDto {
    @NotBlank
    private String roadAddress;
    @Size(max = 25)
    @NotBlank
    private String detailAddress;
    @Size(max = 5)
    @NotBlank
    private String zipCode;


    public AddressDto(Address address) {
        this.roadAddress = address.getRoadAddress();
        this.detailAddress = address.getDetailAddress();
        this.zipCode = address.getZipCode();
    }


}