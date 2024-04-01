package com.study.dahween.user.service;

import com.study.dahween.common.entity.Address;
import com.study.dahween.user.dto.OAuth2UserCreateRequestDto;
import com.study.dahween.user.dto.UserInfoResponseDto;
import com.study.dahween.user.entity.User;
import com.study.dahween.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfoResponseDto getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        return new UserInfoResponseDto(user);
    }

    public void verifyOAuth2Member(String email, OAuth2UserCreateRequestDto requestDto){
        User user = userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        user.update(requestDto.getName(), null, requestDto.getPhone(), Address.toEntity(requestDto.getAddress()));
        user.verifyOAuth2User();
    }

    public UserInfoResponseDto create() {
        User user = User.builder()
                .name("이기욱")
                .email("tizmfns1218@naver.com")
                .build();
        userRepository.save(user);

        return new UserInfoResponseDto(user);
    }
}
