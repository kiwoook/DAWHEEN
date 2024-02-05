package com.study.dahween.volunteer.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
public enum RecruitStatus {
    RECRUITING("모집 중"),
    COMPLETED("모집 완료"),
    PLANNED("모집 예정");

    private final String value;
}
