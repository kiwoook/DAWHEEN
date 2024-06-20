package com.study.dawheen.auth.handler;

import com.study.dawheen.auth.info.CustomOAuth2User;
import com.study.dawheen.auth.jwt.JwtService;
import com.study.dawheen.user.entity.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String AUTHORIZATION_CODE = "Bearer ";
    private final JwtService jwtService;
    @Value("${frontend.server.url}")
    private String frontendUrl;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 처음 로그인이라면 추가 정보를 입력받기 위해 리다이렉트 시킨다.
        if (oAuth2User.getRole() == RoleType.GUEST) {
            String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
            response.addHeader(jwtService.getAccessHeader(), AUTHORIZATION_CODE + accessToken);
            response.sendRedirect(frontendUrl
                    + "/oauth2/sign-up?access_token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8));
            return;
        }
        loginSuccess(response, oAuth2User);

    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) {
        String email = oAuth2User.getEmail();
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        response.addHeader(jwtService.getAccessHeader(), AUTHORIZATION_CODE + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), AUTHORIZATION_CODE + refreshToken);

        jwtService.saveRefreshToken(refreshToken, email);
        jwtService.sendJwtWithRedirect(response, accessToken, refreshToken);
    }

}
