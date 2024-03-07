package com.study.dahween.config;

import com.study.dahween.oauth.exception.RestAuthenticationEntryPoint;
import com.study.dahween.oauth.filter.TokenAuthenticationFilter;
import com.study.dahween.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.study.dahween.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.study.dahween.oauth.handler.TokenAccessDeniedHandler;
import com.study.dahween.oauth.jwt.AuthTokenProvider;
import com.study.dahween.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.study.dahween.oauth.repository.UserRefreshTokenRepository;
import com.study.dahween.oauth.service.CustomOAuth2UserService;
import com.study.dahween.user.entity.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@AllArgsConstructor
public class SecurityConfig {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final CustomOAuth2UserService oAuth2UserService;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final UserRefreshTokenRepository userRefreshTokenRepository;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                // 인증 필요 사이트
                .authorizeHttpRequests(a -> a.requestMatchers("/css/** ", "/images/**", "/js/**", "/swagger-resources/**", "/swagger-ui/**", "/webjars/**", "/swagger/**", "/v3/**")
                        .permitAll()
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        // 어드민 계정만 가능한 api 주소
                        .requestMatchers("/api/v1/admin/**").hasAuthority(RoleType.ADMIN.getCode())
                        // 유저 이상 권한만 가능한 주소
                        .requestMatchers("/api/v1/user/**").hasAnyAuthority(RoleType.MEMBER.getCode(), RoleType.ORGANIZATION.getCode(), RoleType.ADMIN.getCode())
                        .requestMatchers("/api/v1/organ/**").hasAnyAuthority(RoleType.MEMBER.getCode(), RoleType.ORGANIZATION.getCode(), RoleType.ADMIN.getCode())
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(tokenAccessDeniedHandler))
                // oauth2
                .oauth2Login(configurer -> configurer.authorizationEndpoint(config -> config
                                .baseUri("/oauth2/authorization")
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .redirectionEndpoint(config -> config.baseUri("/*/oauth2/code/*"))
                        .userInfoEndpoint(config -> config.userService(oAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                        .failureHandler(oAuth2AuthenticationFailureHandler())

                )
                // UsernamePassword 필터에 대한 검증을 하기 전에 해당 필터를 동작시킨다.
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilterAfter()
                // 해당 필터를 동작후에 발생하게 할 수 있다.
                .build();


    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    /*
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계 하고 검증할 때 사용.
     * */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    /*
     * Oauth 인증 성공 핸들러
     * */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(tokenProvider, appProperties, userRefreshTokenRepository, oAuth2AuthorizationRequestBasedOnCookieRepository());
    }

    /*
     * Oauth 인증 실패 핸들러
     * */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
    }

    /*
     * Cors 설정
     * */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    protected MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new ClientPermissionExpression());
        return expressionHandler;
    }


}