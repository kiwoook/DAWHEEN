package com.study.dahween.volunteer.dto;

import com.study.dahween.volunteer.entity.type.TargetAudience;
import com.study.dahween.volunteer.entity.type.VolunteerType;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * DTO for {@link com.study.dahween.volunteer.entity.VolunteerWork}
 */
@Data
public class VolunteerUpdateResponseDto {
    private String title;
    private String content;
    private LocalDate serviceStartDate;
    private LocalDate serviceEndDate;
    private LocalTime serviceStartTime;
    private LocalTime serviceEndTime;
    private Set<DayOfWeek> serviceDays;
    private Set<TargetAudience> targetAudiences;
    private Set<VolunteerType> volunteerTypes;
    private LocalDateTime recruitStartDateTime;
    private LocalDateTime recruitEndDateTime;
    private Integer maxParticipants;
    private Double longitude;
    private Double latitude;
}