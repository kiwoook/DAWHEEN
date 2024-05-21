package com.study.dawheen.user.repository.impl;

import com.study.dawheen.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private static final String REFRESH_TOKEN_CODE = "REFRESH_TOKEN:";
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String refreshToken, String email) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_CODE+refreshToken, email, 7, TimeUnit.DAYS);
    }

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        redisTemplate.delete(REFRESH_TOKEN_CODE+refreshToken);
    }

    @Override
    public String findByRefreshToken(String refreshToken) {
        return (String)redisTemplate.opsForValue().get(REFRESH_TOKEN_CODE+refreshToken);
    }
}
