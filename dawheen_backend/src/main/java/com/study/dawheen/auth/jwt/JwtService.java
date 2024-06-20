package com.study.dawheen.auth.jwt;

import com.study.dawheen.user.repository.RefreshTokenRepository;
import com.study.dawheen.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Getter
@Slf4j
public class JwtService {

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";
    private static final String QUERY_START_MARK = "?";
    private static final String QUERY_AND_MARK = "&";
    private static final String QUERY_PARAM_ACCESS_TOKEN_KEY = "accessToken=";
    private static final String QUERY_PARAM_REFRESH_TOKEN_KEY = "refreshToken=";
    private final Key key;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.token.expiry}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh-token.expiry}")
    private Long refreshTokenExpirationPeriod;
    @Value("${jwt.token.header}")
    private String accessHeader;
    @Value("${jwt.refresh-token.header}")
    private String refreshHeader;
    @Value("${frontend.server.url}")
    private String frontendUrl;

    public JwtService(UserRepository userRepository, String secretKey, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder().setSubject(ACCESS_TOKEN_SUBJECT).setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod)).claim(EMAIL_CLAIM, email)
                .signWith(key, SignatureAlgorithm.HS256).compact();

    }

    public String createTmpToken(String email) {
        Date now = new Date();

        return Jwts.builder().setSubject(ACCESS_TOKEN_SUBJECT).setExpiration(new Date(now.getTime() + 60 * 5)).claim(EMAIL_CLAIM, email)
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        String uniqueID = UUID.randomUUID().toString();

        return Jwts.builder().setId(uniqueID).setSubject(REFRESH_TOKEN_SUBJECT).setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod)).signWith(key, SignatureAlgorithm.HS256).compact();
    }


    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    public void sendJwtToHeaders(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setHeader(accessHeader, BEARER + accessToken);
        response.setHeader(refreshHeader, BEARER + refreshToken);
    }

    public void sendJwtWithRedirect(HttpServletResponse response, String accessToken, String refreshToken) {
        try {
            response.sendRedirect(generateUrl(accessToken, refreshToken));
            log.info("Access Token, Refresh Token 리다이렉트 완료");
        } catch (IOException e) {
            log.error("Error sending token : {}", e.getMessage());
        }
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }


    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> extractEmail(String accessToken) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken)
                    .getBody();
            log.info(claims.get(EMAIL_CLAIM, String.class));

            return Optional.ofNullable(claims.get(EMAIL_CLAIM, String.class));
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    public void saveRefreshToken(String refreshToken, String email) {
        refreshTokenRepository.save(refreshToken, email);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return true;
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
        }
        return false;
    }


    public boolean isTokenExpired(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return false; // 토큰이 만료되지 않음
        } catch (ExpiredJwtException e) {
            return true; // 토큰이 만료됨
        } catch (Exception e) {
            log.info("isTokenExpired error: {}", e.getMessage());
        }
        return false;
    }

    private String generateUrl(final String accessToken, final String refreshToken) {
        StringBuilder sb = new StringBuilder();
        StringBuilder url = sb.append(frontendUrl)
                .append("/auth-callback")
                .append(QUERY_START_MARK)
                .append(QUERY_PARAM_ACCESS_TOKEN_KEY)
                .append(URLEncoder.encode(accessToken, StandardCharsets.UTF_8))
                .append(QUERY_AND_MARK)
                .append(QUERY_PARAM_REFRESH_TOKEN_KEY)
                .append(URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));
        return url.toString();
    }

}
