package com.study.dahween.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private static final int SUCCESS = 200;
    private static final int NOT_FOUND = 404;
    private static final int FAILED = 500;
    private static final String SUCCESS_MESSAGE = "SUCCESS";
    private static final String NOT_FOUND_MESSAGE = "계정이 존재하지 않습니다.";
    private static final String FAILED_MESSAGE = "서버에서 오류가 발생하였습니다.";
    private static final String INVALID_ACCESS_TOKEN = "Invalid access token.";
    private static final String INVALID_REFRESH_TOKEN = "Invalid refresh token.";
    private static final String NOT_EXPIRED_TOKEN_YET = "Not expired token yet.";

    private final ApiResponseHeader header;
    private final Map<String, T> body;

    public static <T> ApiResponse<T> success(String name, T body) {
        Map<String, T> map = new HashMap<>();
        map.put(name, body);

        return new ApiResponse<>(new ApiResponseHeader(SUCCESS, SUCCESS_MESSAGE), map);
    }

    public static <T> ApiResponse<T> fail() {
        return new ApiResponse<>(new ApiResponseHeader(FAILED, FAILED_MESSAGE), null);
    }

    public static <T> ApiResponse<T> notFound() {
        return new ApiResponse<>(new ApiResponseHeader(NOT_FOUND, NOT_FOUND_MESSAGE), null);
    }

    public static <T> ApiResponse<T> invalidAccessToken() {
        return new ApiResponse<>(new ApiResponseHeader(FAILED, INVALID_ACCESS_TOKEN), null);
    }

    public static <T> ApiResponse<T> invalidRefreshToken() {
        return new ApiResponse<>(new ApiResponseHeader(FAILED, INVALID_REFRESH_TOKEN), null);
    }

    public static <T> ApiResponse<T> notExpiredTokenYet() {
        return new ApiResponse<>(new ApiResponseHeader(FAILED, NOT_EXPIRED_TOKEN_YET), null);
    }
}