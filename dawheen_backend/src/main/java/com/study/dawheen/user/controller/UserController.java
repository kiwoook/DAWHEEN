package com.study.dawheen.user.controller;

import com.study.dawheen.common.dto.TokenResponseDto;
import com.study.dawheen.user.dto.*;
import com.study.dawheen.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저 컨트롤러", description = "유저 정보 관련 API")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
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
        UserInfoResponseDto dto = userService.getUser(email);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "사용자 정보 변경", description = "사용자 정보 변경")
    @PutMapping("/me")
    public ResponseEntity<UserInfoResponseDto> updateUser(@RequestBody @Valid UserUpdateRequestDto requestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInfoResponseDto responseDto = userService.updateUser(email, requestDto);
        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "OAuth2 사용자 등록", description = "추가 정보를 입력받고 해당 OAuth2 유저를 사용자로 등록합니다.")
    @PostMapping("/oauth2")
    public ResponseEntity<TokenResponseDto> verifyOAuth2Member(@RequestBody @Valid OAuth2UserCreateRequestDto requestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        TokenResponseDto responseDto = userService.verifyOAuth2Member(email, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "일반 사용자 등록")
    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponseDto> createUser(@RequestBody @Valid UserCreateRequestDto requestDto) {
        TokenResponseDto responseDto = userService.createUser(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "이메일 확인", description = "존재하는 이메일인지 확인합니다.")
    @GetMapping()
    public ResponseEntity<UserInfoResponseDto> checkEmail(@RequestParam(name = "email") String email) {

        if (userService.notExistEmail(email)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @Operation(summary = "비밀번호 변경", description = "사용자의 이전 비밀번호를 확인하고 비밀번호를 변경합니다.")
    @PutMapping("/password")
    public ResponseEntity<UserInfoResponseDto> changePassword(@RequestBody @Valid UserPasswordChangeRequestDto requestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("email = {}", email);
        log.info("Received Request DTO: {}", requestDto);

        try {
            userService.changePassword(email, requestDto.getOldPassword(), requestDto.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @Operation(summary = "비밀번호 초기화 링크 이메일 전송", description = "사용자의 이메일로 비밀번호 초기화 링크를 전송합니다.")
    @PostMapping("/send-reset-email")
    public ResponseEntity<UserInfoResponseDto> sendResetEmail(@RequestBody @Valid UserResetPasswordRequestDto requestDto) {
        try {
            userService.sendResetEmail(requestDto);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }

    }

    @Operation(summary = "비밀번호 리셋", description = "사용자의 비밀번호를 초기화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보 반환", content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "유저가 로그인하지 않았거나 정보가 없을 시 반환")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<TokenResponseDto> resetPassword(@RequestBody @Valid UserPasswordRequestDto requestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        TokenResponseDto responseDto = userService.resetPassword(email, requestDto.getPassword());
        return ResponseEntity.ok(responseDto);
    }


    @Data
    public static class UserPasswordRequestDto {
        @NotEmpty
        private String password;
    }

}
