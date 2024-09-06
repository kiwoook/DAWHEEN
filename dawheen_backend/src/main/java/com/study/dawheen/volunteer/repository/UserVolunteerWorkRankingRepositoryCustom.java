package com.study.dawheen.volunteer.repository;

import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.volunteer.dto.VolunteerUserRankingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface UserVolunteerWorkRankingRepositoryCustom {

    List<UserInfoResponseDto> getVolunteerActivityRankings(LocalDateTime startDateTime);

    List<VolunteerUserRankingDto> getUserVolunteerCountByPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime);

}
