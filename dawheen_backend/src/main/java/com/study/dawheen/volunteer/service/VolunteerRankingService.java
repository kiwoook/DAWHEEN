package com.study.dawheen.volunteer.service;

import com.study.dawheen.user.dto.UserInfoResponseDto;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.List;

public interface VolunteerRankingService {

    String MONTHLY_RANKING = "monthly-ranking";
    String SEMI_ANNUAL_RANKING = "semi-annual-ranking";
    String ANNUAL_RANKING = "annual-ranking";

    List<UserInfoResponseDto> getMonthlyRanking() throws IOException, EntityNotFoundException;

    List<UserInfoResponseDto> getSemiAnnualRanking() throws IOException, EntityNotFoundException;

    List<UserInfoResponseDto> getAnnualRanking() throws IOException, EntityNotFoundException;


}
