package com.study.dahween.user.controller;

import com.study.dahween.user.dto.UserInfoResponseDto;
import com.study.dahween.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyProfile() {
        // 유저 자신의 정보를 반환합니다.
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = principal.getUsername();

        try {
            UserInfoResponseDto dto = userService.getUser(userId);

            return ResponseEntity.ok(dto);
        }catch (EntityNotFoundException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDto> getUserProfile(@PathVariable String userId) {
        try {
            UserInfoResponseDto dto = userService.getUser(userId);
            return ResponseEntity.ok(dto);
        }catch (EntityNotFoundException e){
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO 유저의 자원봉사 내역


}
