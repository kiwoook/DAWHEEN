package com.study.dahween.volunteer.repository;

import com.study.dahween.volunteer.entity.VolunteerWork;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import com.study.dahween.volunteer.entity.type.TargetAudience;
import com.study.dahween.volunteer.entity.type.VolunteerType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VolunteerWorkRepositoryCustom {
    Optional<List<VolunteerWork>> getByRadiusAndBeforeEndDate(double latitude, double longitude, int radius);


    // 필터에 맞춰 VolunteerWork 반환
    Optional<List<VolunteerWork>> getByFiltersAndDataRangeWithinRadius(
            double latitude, double longitude, int radius,
            Set<VolunteerType> volunteerTypes, Set<TargetAudience> targetAudiences,
            LocalDate startDate, LocalDate endDate
    );

    // 봉사활동 ID와 STATUS 로 개수 찾기
    int countAllByVolunteerWorkIdAndStatus(Long volunteerWorkId, ApplyStatus status);
}
