package com.study.dawheen.admin.controller;

import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "유저 검색", description = "유저의 이메일로 검색하여 반환합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<UserInfoResponseDto> getUserProfile(@PathVariable String email) {
        UserInfoResponseDto dto = userService.getUser(email);
        return ResponseEntity.ok(dto);
    }
}
