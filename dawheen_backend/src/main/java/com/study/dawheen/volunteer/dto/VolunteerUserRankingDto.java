package com.study.dawheen.volunteer.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VolunteerUserRankingDto {

    String userEmail;
    Double count;

    public VolunteerUserRankingDto(String userEmail, Double count) {
        this.userEmail = userEmail;
        this.count = count;
    }

    public VolunteerUserRankingDto(String userEmail, Long count) {
        this.userEmail = userEmail;
        this.count = count.doubleValue();
    }

}
