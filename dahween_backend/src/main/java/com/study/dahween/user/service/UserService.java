package com.study.dahween.user.service;

import com.study.dahween.auth.JwtResponseDto;
import com.study.dahween.auth.jwt.JwtService;
import com.study.dahween.common.entity.Address;
import com.study.dahween.mail.service.MailService;
import com.study.dahween.user.dto.OAuth2UserCreateRequestDto;
import com.study.dahween.user.dto.UserInfoResponseDto;
import com.study.dahween.user.dto.UserResetPasswordRequestDto;
import com.study.dahween.user.entity.User;
import com.study.dahween.user.repository.UserRepository;
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

    public UserInfoResponseDto getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        return new UserInfoResponseDto(user);
    }

    @Transactional
    public void verifyOAuth2Member(String email, OAuth2UserCreateRequestDto requestDto) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        user.update(requestDto.getName(), null, requestDto.getPhone(), Address.toEntity(requestDto.getAddress()));
        user.verifyOAuth2User();
    }

    public boolean checkEmail(String email){
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

    public void sendResetEmail(UserResetPasswordRequestDto requestDto) throws Exception {
        if (!userRepository.existsByEmailAndName(requestDto.getEmail(), requestDto.getName())){
            throw new IllegalStateException();
        }

        mailService.sendResetPassword(requestDto.getEmail());
    }
    @Transactional
    public JwtResponseDto resetPassword(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        user.changePassword(password);

        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        user.updateRefreshToken(refreshToken);

        return new JwtResponseDto(accessToken, refreshToken);
    }
}
