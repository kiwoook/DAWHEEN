package com.study.dawheen.config;

import com.study.dawheen.auth.jwt.JwtService;
import com.study.dawheen.user.repository.RefreshTokenRepository;
import com.study.dawheen.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JwtConfig {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public JwtService jwtService() {
        return new JwtService(userRepository, secret, refreshTokenRepository);
    }
}