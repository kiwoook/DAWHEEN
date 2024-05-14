package com.study.dawheen.common.dto;

import com.study.dawheen.common.entity.Coordinate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoordinateInfoResponseDto {

    private Double latitude;
    private Double longitude;

    public CoordinateInfoResponseDto(Coordinate coordinate) {
        this.latitude = coordinate.getLatitude();
        this.longitude = coordinate.getLongitude();
    }
}
