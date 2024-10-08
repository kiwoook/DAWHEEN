package com.study.dawheen.volunteer.controller;

import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.volunteer.service.VolunteerRankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Tag(name = "봉사활동 랭킹 API", description = "봉사활동 랭킹 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/volunteer/ranking")
public class VolunteerRankingController {

    private final VolunteerRankingService rankingService;

    @GetMapping("/monthly")
    public ResponseEntity<List<UserInfoResponseDto>> getMonthlyRanking() throws InterruptedException, IOException {
        List<UserInfoResponseDto> responseDtoList = rankingService.getMonthlyRanking();
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/semi-annual")
    public ResponseEntity<List<UserInfoResponseDto>> getSemiAnnualRanking() throws InterruptedException, IOException {
        List<UserInfoResponseDto> responseDtoList = rankingService.getSemiAnnualRanking();
        return ResponseEntity.ok(responseDtoList);

    }

    @GetMapping("/annual")
    public ResponseEntity<List<UserInfoResponseDto>> getAnnualRanking() throws InterruptedException, IOException {
        List<UserInfoResponseDto> responseDtoList = rankingService.getAnnualRanking();
        return ResponseEntity.ok(responseDtoList);
    }
}
