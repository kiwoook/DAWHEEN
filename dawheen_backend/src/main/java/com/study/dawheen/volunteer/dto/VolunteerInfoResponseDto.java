package com.study.dawheen.volunteer.dto;

import com.study.dawheen.common.dto.CoordinateDto;
import com.study.dawheen.organization.dto.OrganInfoResponseDto;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;


@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class VolunteerInfoResponseDto {
    private int appliedParticipants;
    private int maxParticipants;
    private Long id;
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
    private CoordinateDto coordinateDto;

    public VolunteerInfoResponseDto(int appliedParticipants, int maxParticipants, Long id, LocalDateTime createdDate, LocalDateTime modifiedDate, OrganInfoResponseDto organInfoResponseDto, String title, String content, LocalDateTime serviceStartDatetime, LocalDateTime serviceEndDatetime, Set<DayOfWeek> serviceDays, Set<TargetAudience> targetAudiences, Set<VolunteerType> volunteerTypes, LocalDateTime recruitStartDateTime, LocalDateTime recruitEndDateTime, CoordinateDto coordinateDto) {
        this.appliedParticipants = appliedParticipants;
        this.maxParticipants = maxParticipants;
        this.id = id;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.organInfoResponseDto = organInfoResponseDto;
        this.title = title;
        this.content = content;
        this.serviceStartDatetime = serviceStartDatetime;
        this.serviceEndDatetime = serviceEndDatetime;
        this.serviceDays = serviceDays;
        this.targetAudiences = targetAudiences;
        this.volunteerTypes = volunteerTypes;
        this.recruitStartDateTime = recruitStartDateTime;
        this.recruitEndDateTime = recruitEndDateTime;
        this.coordinateDto = coordinateDto;
    }

    public VolunteerInfoResponseDto(VolunteerWork volunteerWork) {
        this.id = volunteerWork.getId();
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
        this.organInfoResponseDto = OrganInfoResponseDto.toDto(volunteerWork.getOrganization());
        this.coordinateDto = CoordinateDto.toDto(volunteerWork.getCoordinate());
    }



    public static VolunteerInfoResponseDto toDto(VolunteerWork volunteerWork) {
        if (volunteerWork == null) {
            return null;
        }

        return new VolunteerInfoResponseDto(volunteerWork);
    }
}