package com.study.dahween.config;

import com.study.dahween.oauth.exception.RestAuthenticationEntryPoint;
import com.study.dahween.oauth.handler.TokenAccessDeniedHandler;
import com.study.dahween.oauth.jwt.AuthTokenProvider;
import com.study.dahween.oauth.repository.UserRefreshTokenRepository;
import com.study.dahween.oauth.service.CustomOAuth2UserService;
import com.study.dahween.oauth.service.CustomUserDetailsService;
import com.study.dahween.user.entity.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CorsProperties corsProperties;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final CustomOAuth2UserService oAuth2UserService;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final UserRefreshTokenRepository userRefreshTokenRepository;


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        // 스프링 시큐리티 예외처리
        return (web) -> web.ignoring().requestMatchers("/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**","/v2/api-docs");
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 인증 필요 사이트
                .authorizeHttpRequests(a -> a.requestMatchers("/css/** ","/images/**", "/js/**").permitAll()
                        // 어드민 계정만 가능한 api 주소
                        .requestMatchers("/api/admin/**").hasAuthority(RoleType.ADMIN.getCode())
                        // 유저 이상 권한만 가능한 주소
                        .requestMatchers("/api/user/**").hasAnyAuthority(RoleType.MEMBER.getCode())
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(new RestAuthenticationEntryPoint()).accessDeniedHandler(tokenProvider))


                .userDetailsService(userDetailsService)
                .build();




    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}