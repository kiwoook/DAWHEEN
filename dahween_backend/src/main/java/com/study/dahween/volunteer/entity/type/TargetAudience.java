package com.study.dahween.volunteer.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TargetAudience {
    CHILD_YOUTH("아동·청소년"),
    DISABLED("장애인"),
    ELDERLY("노인"),
    MULTICULTURAL_FAMILY("다문화가정"),
    ENVIRONMENT("환경"),
    SOCIAL_ENTERPRISE("사회적 기업"),
    ANIMAL("동물"),
    OTHER("기타");

    private final String value;
}
