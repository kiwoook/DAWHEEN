package com.study.dahween.volunteer.dto;

import com.study.dahween.common.dto.CoordinateInfoResponseDto;
import com.study.dahween.organization.dto.OrganInfoResponseDto;
import com.study.dahween.volunteer.entity.VolunteerWork;
import com.study.dahween.volunteer.entity.type.TargetAudience;
import com.study.dahween.volunteer.entity.type.VolunteerType;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;


@Data
public class VolunteerInfoResponseDto {
    int appliedParticipants;
    int maxParticipants;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private OrganInfoResponseDto organInfoResponseDto;
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
    private CoordinateInfoResponseDto coordinateInfoResponseDto;

    public VolunteerInfoResponseDto(VolunteerWork volunteerWork) {
        this.createdDate = volunteerWork.getCreatedDate();
        this.modifiedDate = volunteerWork.getModifiedDate();
        this.organInfoResponseDto = new OrganInfoResponseDto(volunteerWork.getOrganization());
        this.title = volunteerWork.getTitle();
        this.content = volunteerWork.getContent();
        this.serviceStartDate = volunteerWork.getServiceStartDate();
        this.serviceEndDate = volunteerWork.getServiceEndDate();
        this.serviceStartTime = volunteerWork.getServiceStartTime();
        this.serviceEndTime = volunteerWork.getServiceEndTime();
        this.serviceDays = volunteerWork.getServiceDays();
        this.targetAudiences = volunteerWork.getTargetAudiences();
        this.volunteerTypes = volunteerWork.getVolunteerTypes();
        this.recruitStartDateTime = volunteerWork.getRecruitStartDateTime();
        this.recruitEndDateTime = volunteerWork.getRecruitEndDateTime();
        this.appliedParticipants = volunteerWork.getAppliedParticipants().get();
        this.maxParticipants = volunteerWork.getMaxParticipants();
        this.coordinateInfoResponseDto = new CoordinateInfoResponseDto(volunteerWork.getCoordinate());
    }
}