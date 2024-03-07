package com.study.dahween.common.dto;

import com.study.dahween.common.entity.Coordinate;
import com.study.dahween.volunteer.entity.VolunteerWork;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoordinateInfoResponseDto {

    private Double latitude;
    private Double longitude;

    public CoordinateInfoResponseDto(Coordinate coordinate) {
        this.latitude = coordinate.getLatitude();
        this.longitude = coordinate.getLongitude();
    }
}
