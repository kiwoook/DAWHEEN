package com.study.dawheen.user.controller;

import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.service.UserVolunteerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 유저의 자원봉사 관련 컨트롤러
 */

@Tag(name = "유저 API - 자원봉사", description = "유저의 자원봉사 내역 관련")
@RestController
@RequestMapping("/api/v1/user/volunteer")
@RequiredArgsConstructor
public class UserVolunteerController {

    private final UserVolunteerService userVolunteerService;

    public ResponseEntity<Page<VolunteerInfoResponseDto>> getMyVolunteerInfo(@PageableDefault(page = 1, size = 20) Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Page<VolunteerInfoResponseDto> responseDtos = userVolunteerService.getParticipateVolunteerWorkByUser(email, pageable);
        return ResponseEntity.ok(responseDtos);
    }
}
