package com.study.dawheen.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    // TODO 유저의 자원봉사 내역 페이지네이션하기

}
