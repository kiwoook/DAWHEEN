package com.study.dawheen.volunteer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.List;

public interface VolunteerRankingService {



    void addVolunteerUser(String email) throws JsonProcessingException, InterruptedException;

    void fetchRankingData();

    List<UserInfoResponseDto> getMonthlyRanking() throws IOException, EntityNotFoundException, InterruptedException;

    List<UserInfoResponseDto> getSemiAnnualRanking() throws IOException, EntityNotFoundException, InterruptedException;

    List<UserInfoResponseDto> getAnnualRanking() throws IOException, EntityNotFoundException, InterruptedException;


}
