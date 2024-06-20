package com.study.dawheen.auth.filter;

import com.study.dawheen.auth.jwt.JwtService;
import com.study.dawheen.auth.utils.PasswordUtil;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.RefreshTokenRepository;
import com.study.dawheen.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilterV2 extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/api/v1/login";
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // "/login" 요청이 들어오면, 다음 필터 호출
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 액세스 토큰을 먼저 검증한다.
        checkAccessToken(request, response, filterChain);

    }

    private void checkAccessToken(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessToken () 호출");

        String accessToken = jwtService.extractAccessToken(request).orElse(null);
        // 유효한 토큰인지 확인한 다음 만료된 토큰이라면
        // 리프레쉬 토큰을 체킹한다.

        if (accessToken != null && jwtService.isTokenExpired(accessToken)) {
            checkRefreshToken(request, response);
            return;
        }

        jwtService.extractEmail(accessToken).flatMap(userRepository::findByEmail).ifPresent(this::saveAuthentication);
        filterChain.doFilter(request, response);
    }

    private void checkRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (refreshToken == null) {
            return;
        }

        // 해당 토큰으로 이메일을 가져온 후에 이메일로 새로운 토큰을 발급한다.
        // 만약 유효하지 않은 토큰이라면 넘긴다.
        // 유효하다면 기존의 리프레쉬 키는 삭제한다.
        String email = refreshTokenRepository.findByRefreshToken(refreshToken);

        log.info("반환된 이메일 키 확인 : {}", email);
        if (email != null) {
            String reIssuedRefreshToken = reIssueRefreshToken(refreshToken, email);
            jwtService.sendJwtToHeaders(response, jwtService.createAccessToken(email), reIssuedRefreshToken);
            return;
        }

        // Redis 에서 해당 키가 존재하지 않을 경우 null 반환
        log.info("유효하지 않은 리프레쉬 토큰 = {}", refreshToken);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Refresh Token is invalid");

    }

    private String reIssueRefreshToken(String refreshToken, String email) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.saveRefreshToken(reIssuedRefreshToken, email);
        return reIssuedRefreshToken;
    }

    private void saveAuthentication(User myUser) {
        String password = myUser.getPassword();

        if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
            password = PasswordUtil.generateRandomPassword();
        }


        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(password)
                .roles(myUser.getRole().getCode())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
