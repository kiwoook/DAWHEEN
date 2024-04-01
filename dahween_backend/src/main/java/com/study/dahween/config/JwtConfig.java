package com.study.dahween.config;

import com.study.dahween.auth.jwt.JwtService;
import com.study.dahween.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JwtConfig {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public JwtService jwtService() {
        return new JwtService(userRepository, secret);
    }
}