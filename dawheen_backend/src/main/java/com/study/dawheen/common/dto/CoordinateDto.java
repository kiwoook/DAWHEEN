package com.study.dawheen.common.dto;

import com.study.dawheen.common.entity.Coordinate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link Coordinate}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoordinateDto {
    Double latitude;
    Double longitude;
}