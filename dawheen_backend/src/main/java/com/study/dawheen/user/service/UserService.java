package com.study.dawheen.user.service;

import com.study.dawheen.auth.JwtResponseDto;
import com.study.dawheen.auth.jwt.JwtService;
import com.study.dawheen.common.entity.Address;
import com.study.dawheen.infra.mail.MailService;
import com.study.dawheen.user.dto.OAuth2UserCreateRequestDto;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.dto.UserResetPasswordRequestDto;
import com.study.dawheen.user.dto.UserUpdateRequestDto;
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

    public UserInfoResponseDto getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        return new UserInfoResponseDto(user);
    }

    public UserInfoResponseDto updateUser(String email, UserUpdateRequestDto requestDto){
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        user.update(requestDto.getName(), requestDto.getEmail(), requestDto.getPhone(), Address.toEntity(requestDto.getAddress()));

        return new UserInfoResponseDto(userRepository.save(user));
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
