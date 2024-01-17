package com.study.dahween.organization.controller;

import com.study.dahween.organization.service.OrganService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganController {
    private final OrganService organService;

    // 기관 정보 확인

    // 기관 개설

    // 기관 수정

    // 기관 삭제

    // 해당 기관의 후기 모음

}
