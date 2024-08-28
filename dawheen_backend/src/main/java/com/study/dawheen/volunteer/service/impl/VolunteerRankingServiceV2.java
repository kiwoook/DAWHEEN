package com.study.dawheen.volunteer.service.impl;

import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.volunteer.service.VolunteerRankingService;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.List;

public class VolunteerRankingServiceV2 implements VolunteerRankingService {

    public void completeUser(){

    }

    @Override
    public List<UserInfoResponseDto> getMonthlyRanking() throws IOException, EntityNotFoundException {
        return List.of();
    }

    @Override
    public List<UserInfoResponseDto> getSemiAnnualRanking() throws IOException, EntityNotFoundException {
        return List.of();
    }

    @Override
    public List<UserInfoResponseDto> getAnnualRanking() throws IOException, EntityNotFoundException {
        return List.of();
    }
}
