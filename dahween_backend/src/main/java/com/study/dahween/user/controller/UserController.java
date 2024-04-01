package com.study.dahween.user.controller;

import com.study.dahween.user.dto.OAuth2UserCreateRequestDto;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            UserInfoResponseDto dto = userService.getUser(email);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/oauth2")
    public ResponseEntity<UserInfoResponseDto> verifyOAuth2Member(@RequestBody OAuth2UserCreateRequestDto requestDto){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        try{
            userService.verifyOAuth2Member(email, requestDto);
            return ResponseEntity.ok().build();
        }catch (EntityNotFoundException e){
            return ResponseEntity.badRequest().build();
        }
    }



    // TODO 유저의 자원봉사 내역


}
