package com.study.dahween.volunteer.entity.type;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VolunteerType {
    ADULT("성인"),
    YOUTH("청소년");

    private final String value;
}
