package com.study.dawheen.volunteer.repository;

import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface VolunteerWorkRepositoryCustom {
    List<VolunteerInfoResponseDto> getByRadiusAndBeforeEndDate(double latitude, double longitude, double radius);


    // 필터에 맞춰 VolunteerWork 반환
    List<VolunteerInfoResponseDto> getByFiltersAndDataRangeWithinRadius(
            double latitude, double longitude, double radius,
            Set<VolunteerType> volunteerTypes, Set<TargetAudience> targetAudiences,
            LocalDateTime startDate, LocalDateTime endDate
    );


}
