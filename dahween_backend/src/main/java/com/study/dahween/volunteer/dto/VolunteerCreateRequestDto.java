package com.study.dahween.volunteer.dto;

import com.study.dahween.volunteer.entity.type.TargetAudience;
import com.study.dahween.volunteer.entity.type.VolunteerType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;


@Data
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


}