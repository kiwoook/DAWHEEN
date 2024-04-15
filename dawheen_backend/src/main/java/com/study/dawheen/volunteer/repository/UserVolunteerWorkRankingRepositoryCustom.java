package com.study.dawheen.volunteer.repository;

import com.study.dawheen.user.dto.UserInfoResponseDto;

import java.util.List;

public interface UserVolunteerWorkRankingRepositoryCustom {

    List<UserInfoResponseDto> getMonthlyVolunteerActivityRankings();

    List<UserInfoResponseDto> getSemiAnnualVolunteerActivityRankings();

    List<UserInfoResponseDto> getYearlyVolunteerActivityRankings();
}
