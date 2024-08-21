package com.study.dawheen.volunteer.dto;

import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * DTO for {@link com.study.dawheen.volunteer.entity.VolunteerWork}
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VolunteerUpdateRequestDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    @NotNull
    private LocalDate serviceStartDate;
    @NotNull
    private LocalDate serviceEndDate;
    private LocalTime serviceStartTime;
    private LocalTime serviceEndTime;
    private Set<DayOfWeek> serviceDays;
    private Set<TargetAudience> targetAudiences;
    private Set<VolunteerType> volunteerTypes;
    @NotNull
    private LocalDateTime recruitStartDateTime;
    @NotNull
    private LocalDateTime recruitEndDateTime;
    private Integer maxParticipants;
    private Double longitude;
    private Double latitude;
}