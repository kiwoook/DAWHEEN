package com.study.dawheen.user.service;

import com.study.dawheen.auth.JwtResponseDto;
import com.study.dawheen.auth.jwt.JwtService;
import com.study.dawheen.common.dto.TokenResponseDto;
import com.study.dawheen.common.entity.Address;
import com.study.dawheen.infra.mail.MailService;
import com.study.dawheen.user.dto.*;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final JwtService jwtService;

    @Transactional
    public TokenResponseDto createUser(UserCreateRequestDto requestDto){
        String email = requestDto.getEmail();
        User user = User.builder()
                .email(email)
                .password(requestDto.getPassword())
                .name(requestDto.getName())
                .roleType(RoleType.MEMBER)
                .build();
        userRepository.save(user);

        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();
        jwtService.saveRefreshToken(refreshToken, email);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public UserInfoResponseDto getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        return new UserInfoResponseDto(user);
    }

    @Transactional
    public UserInfoResponseDto updateUser(String email, UserUpdateRequestDto requestDto) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        user.update(requestDto.getName(), requestDto.getEmail(), requestDto.getPhone(), Address.toEntity(requestDto.getAddress()));

        return new UserInfoResponseDto(userRepository.save(user));
    }

    @Transactional
    public TokenResponseDto verifyOAuth2Member(String email, OAuth2UserCreateRequestDto requestDto) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        user.update(requestDto.getName(), null, requestDto.getPhone(), Address.toEntity(requestDto.getAddress()));
        user.verifyOAuth2User();

        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();
        jwtService.saveRefreshToken(refreshToken, email);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public boolean notExistEmail(String email) {
        // True 시 이메일이 존재하지 않는 것
        return !userRepository.existsByEmail(email);
    }

    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        // 이전 비밀번호 확인
        if (!user.equalPassword(oldPassword)) {
            throw new IllegalStateException();
        }

        user.changePassword(newPassword);
    }

    public void sendResetEmail(UserResetPasswordRequestDto requestDto) {
        if (!userRepository.existsByEmailAndName(requestDto.getEmail(), requestDto.getName())) {
            throw new IllegalStateException();
        }
        mailService.sendResetPassword(requestDto.getEmail());
    }

    @Transactional
    public JwtResponseDto resetPassword(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        user.changePassword(password);

        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();
        jwtService.saveRefreshToken(refreshToken, email);

        return new JwtResponseDto(accessToken, refreshToken);
    }
}
