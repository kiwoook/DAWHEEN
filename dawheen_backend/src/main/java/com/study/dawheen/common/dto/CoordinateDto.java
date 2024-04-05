package com.study.dawheen.common.dto;

import com.study.dawheen.common.entity.Coordinate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for {@link Coordinate}
 */
@Data
@AllArgsConstructor
public class CoordinateDto {
    @NotNull
    Double latitude;
    @NotNull
    Double longitude;


}