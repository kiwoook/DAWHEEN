package com.study.dahween.common.dto;

import com.study.dahween.common.entity.Coordinate;
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