package com.study.dawheen.common.dto;

import com.study.dawheen.common.entity.Coordinate;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    Double latitude;
    @NotNull
    Double longitude;

}