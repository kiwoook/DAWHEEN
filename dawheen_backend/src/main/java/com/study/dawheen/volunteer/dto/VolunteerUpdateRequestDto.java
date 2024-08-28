package com.study.dawheen.volunteer.dto;

import com.study.dawheen.common.dto.CoordinateDto;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
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
    private LocalDateTime serviceStartDatetime;
    @NotNull
    private LocalDateTime serviceEndDatetime;
    private Set<DayOfWeek> serviceDays;
    private Set<TargetAudience> targetAudiences;
    private Set<VolunteerType> volunteerTypes;
    @NotNull
    private LocalDateTime recruitStartDateTime;
    @NotNull
    private LocalDateTime recruitEndDateTime;
    private Integer maxParticipants;
    private CoordinateDto coordinate;

    public VolunteerUpdateRequestDto(String title, String content, LocalDateTime serviceStartDatetime, LocalDateTime serviceEndDatetime, Set<DayOfWeek> serviceDays, Set<TargetAudience> targetAudiences, Set<VolunteerType> volunteerTypes, LocalDateTime recruitStartDateTime, LocalDateTime recruitEndDateTime, Integer maxParticipants, Double longitude, Double latitude) {
        this.title = title;
        this.content = content;
        this.serviceStartDatetime = serviceStartDatetime;
        this.serviceEndDatetime = serviceEndDatetime;
        this.serviceDays = serviceDays;
        this.targetAudiences = targetAudiences;
        this.volunteerTypes = volunteerTypes;
        this.recruitStartDateTime = recruitStartDateTime;
        this.recruitEndDateTime = recruitEndDateTime;
        this.maxParticipants = maxParticipants;
        this.coordinate = new CoordinateDto(latitude, longitude);
    }
}