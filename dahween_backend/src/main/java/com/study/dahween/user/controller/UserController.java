package com.study.dahween.user.controller;

import com.study.dahween.user.dto.UserInfoResponseDto;
import com.study.dahween.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "사용자 정보 반환", description = "사용자의 유저 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보 반환", content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "유저가 로그인하지 않았거나 정보가 없을 시 반환")
    })
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyProfile() {
        // 유저 자신의 정보를 반환합니다.
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = principal.getUsername();

        try {
            UserInfoResponseDto dto = userService.getUser(userId);

            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "유저 검색", description = "유저의 ID를 검색하여 반환합니다.")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDto> getUserProfile(@PathVariable String userId) {
        try {
            UserInfoResponseDto dto = userService.getUser(userId);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO 유저의 자원봉사 내역


}
