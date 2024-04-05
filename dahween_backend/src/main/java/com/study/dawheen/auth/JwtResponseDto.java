package com.study.dawheen.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDto {

    private String accessToken;

    private String refreshToken;
}
