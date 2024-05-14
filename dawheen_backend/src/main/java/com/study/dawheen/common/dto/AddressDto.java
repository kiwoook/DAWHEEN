package com.study.dawheen.common.dto;

import com.study.dawheen.common.entity.Address;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link com.study.dawheen.common.entity.Address}
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddressDto {

    private String roadAddress;
    @Size(max = 25)
    private String detailAddress;
    @Size(max = 5)
    private String zipCode;


    public AddressDto(Address address) {
        this.roadAddress = address.getRoadAddress();
        this.detailAddress = address.getDetailAddress();
        this.zipCode = address.getZipCode();
    }


}