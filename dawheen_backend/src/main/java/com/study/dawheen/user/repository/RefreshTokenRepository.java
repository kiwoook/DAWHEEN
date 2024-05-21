package com.study.dawheen.user.repository;

public interface RefreshTokenRepository {

    void save(String refreshToken, String email);

    void deleteByRefreshToken(String refreshToken);

    String findByRefreshToken(String refreshToken);
}
