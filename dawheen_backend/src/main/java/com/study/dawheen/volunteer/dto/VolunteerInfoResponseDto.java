package com.study.dawheen.volunteer.dto;

import com.study.dawheen.common.dto.CoordinateInfoResponseDto;
import com.study.dawheen.organization.dto.OrganInfoResponseDto;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;


@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VolunteerInfoResponseDto {
    int appliedParticipants;
    int maxParticipants;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private OrganInfoResponseDto organInfoResponseDto;
    private String title;
    private String content;
    private LocalDateTime serviceStartDatetime;
    private LocalDateTime serviceEndDatetime;
    private Set<DayOfWeek> serviceDays;
    private Set<TargetAudience> targetAudiences;
    private Set<VolunteerType> volunteerTypes;
    private LocalDateTime recruitStartDateTime;
    private LocalDateTime recruitEndDateTime;
    private CoordinateInfoResponseDto coordinateInfoResponseDto;


    public VolunteerInfoResponseDto(VolunteerWork volunteerWork) {
        this.createdDate = volunteerWork.getCreatedDate();
        this.modifiedDate = volunteerWork.getModifiedDate();
        this.title = volunteerWork.getTitle();
        this.content = volunteerWork.getContent();
        this.serviceStartDatetime = volunteerWork.getServiceStartDatetime();
        this.serviceEndDatetime = volunteerWork.getServiceEndDatetime();
        this.serviceDays = volunteerWork.getServiceDays();
        this.targetAudiences = volunteerWork.getTargetAudiences();
        this.volunteerTypes = volunteerWork.getVolunteerTypes();
        this.recruitStartDateTime = volunteerWork.getRecruitStartDateTime();
        this.recruitEndDateTime = volunteerWork.getRecruitEndDateTime();
        this.appliedParticipants = volunteerWork.getAppliedParticipants().get();
        this.maxParticipants = volunteerWork.getMaxParticipants();
        this.organInfoResponseDto = Optional.ofNullable(volunteerWork.getOrganization())
                .map(OrganInfoResponseDto::new)
                .orElse(null);
        this.coordinateInfoResponseDto = Optional.ofNullable(volunteerWork.getCoordinate())
                .map(CoordinateInfoResponseDto::new)
                .orElse(null);
    }
}