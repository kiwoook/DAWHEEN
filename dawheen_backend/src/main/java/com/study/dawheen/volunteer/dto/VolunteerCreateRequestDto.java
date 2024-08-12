package com.study.dawheen.volunteer.dto;

import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;


@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class VolunteerCreateRequestDto {
    @NotBlank
    String title;
    @NotNull
    String content;
    @FutureOrPresent
    LocalDate serviceStartDate;
    @FutureOrPresent
    LocalDate serviceEndDate;
    @FutureOrPresent
    LocalTime serviceStartTime;
    @FutureOrPresent
    LocalTime serviceEndTime;
    @NotNull
    Set<DayOfWeek> serviceDays;
    @FutureOrPresent
    LocalDateTime recruitStartDateTime;
    @FutureOrPresent
    LocalDateTime recruitEndDateTime;
    @Positive
    int maxParticipants;
    @NotNull
    Set<TargetAudience> targetAudiences;
    @NotNull
    Set<VolunteerType> volunteerTypes;

    public VolunteerCreateRequestDto(String title, String content, LocalDate serviceStartDate, LocalDate serviceEndDate, LocalTime serviceStartTime, LocalTime serviceEndTime, Set<DayOfWeek> serviceDays, LocalDateTime recruitStartDateTime, LocalDateTime recruitEndDateTime, int maxParticipants, Set<TargetAudience> targetAudiences, Set<VolunteerType> volunteerTypes) {
        this.title = title;
        this.content = content;
        this.serviceStartDate = serviceStartDate;
        this.serviceEndDate = serviceEndDate;
        this.serviceStartTime = serviceStartTime;
        this.serviceEndTime = serviceEndTime;
        this.serviceDays = serviceDays;
        this.recruitStartDateTime = recruitStartDateTime;
        this.recruitEndDateTime = recruitEndDateTime;
        this.maxParticipants = maxParticipants;
        this.targetAudiences = targetAudiences;
        this.volunteerTypes = volunteerTypes;
    }
}