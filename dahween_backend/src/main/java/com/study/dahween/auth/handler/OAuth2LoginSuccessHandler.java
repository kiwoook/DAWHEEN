package com.study.dahween.auth.handler;

import com.study.dahween.auth.info.CustomOAuth2User;
import com.study.dahween.auth.jwt.JwtService;
import com.study.dahween.user.entity.RoleType;
import com.study.dahween.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String AUTHORIZATION_CODE = "Bearer ";
    private static final String QUERY_START_MARK = "?";
    private static final String QUERY_AND_MARK = "&";
    private static final String QUERY_PARAM_ACCESS_TOKEN_KEY = "accessToken=";
    private static final String QUERY_PARAM_REFRESH_TOKEN_KEY = "refreshToken=";
    private final JwtService jwtService;
    private final UserRepository userRepository;
    @Value("${frontend.server.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        if (oAuth2User.getRole() == RoleType.GUEST) {
            String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
            response.addHeader(jwtService.getAccessHeader(), AUTHORIZATION_CODE + accessToken);
            response.sendRedirect(frontendUrl
                    + "/oauth2/sign-up");
            return;
        }
        loginSuccess(response, oAuth2User);

    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String email = oAuth2User.getEmail();
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);
                });
        response.addHeader(jwtService.getAccessHeader(), AUTHORIZATION_CODE + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), AUTHORIZATION_CODE + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);

        response.sendRedirect(generateUrl(accessToken, refreshToken));
    }

    private String generateUrl(final String accessToken, final String refreshToken) {
        StringBuilder sb = new StringBuilder();
        StringBuilder url = sb.append(frontendUrl)
                .append("/oauth2-callback")
                .append(QUERY_START_MARK)
                .append(QUERY_PARAM_ACCESS_TOKEN_KEY)
                .append(accessToken)
                .append(QUERY_AND_MARK)
                .append(QUERY_PARAM_REFRESH_TOKEN_KEY)
                .append(refreshToken);
        return url.toString();
    }
}
