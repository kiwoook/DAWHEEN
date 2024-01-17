package com.study.dahween.user.service;

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

    public UserInfoResponseDto getUser(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);

        return new UserInfoResponseDto(user);
    }
}
