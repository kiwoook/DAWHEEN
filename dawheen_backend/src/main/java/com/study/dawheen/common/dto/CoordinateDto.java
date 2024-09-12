package com.study.dawheen.common.dto;

import com.study.dawheen.common.entity.Coordinate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link Coordinate}
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoordinateDto {
    Double latitude;
    Double longitude;

    public CoordinateDto(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static CoordinateDto toDto(Coordinate coordinate) {
        if (coordinate == null) {
            return null;
        }

        return new CoordinateDto(coordinate.getLatitude(), coordinate.getLongitude());
    }
}