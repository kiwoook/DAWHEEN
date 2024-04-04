package com.study.dahween.user.controller;

import com.study.dahween.auth.JwtResponseDto;
import com.study.dahween.mail.service.MailService;
import com.study.dahween.user.dto.OAuth2UserCreateRequestDto;
import com.study.dahween.user.dto.UserInfoResponseDto;
import com.study.dahween.user.dto.UserResetPasswordRequestDto;
import com.study.dahween.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final MailService mailService;

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

    @Operation(summary = "OAuth2 사용자 추가", description = "추가 정보를 입력받고 해당 OAuth2 유저를 사용자로 등록합니다.")
    @PostMapping("/oauth2")
    public ResponseEntity<UserInfoResponseDto> verifyOAuth2Member(@RequestBody OAuth2UserCreateRequestDto requestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            userService.verifyOAuth2Member(email, requestDto);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "이메일 확인", description = "존재하는 이메일인지 확인합니다.")
    @GetMapping()
    public ResponseEntity<UserInfoResponseDto> checkEmail(@RequestParam(name = "email") String email) {

        if (userService.checkEmail(email)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }


    @Operation(summary = "비밀번호 변경", description = "사용자의 이전 비밀번호를 확인하고 비밀번호를 변경합니다.")
    @PutMapping("/password")
    public ResponseEntity<UserInfoResponseDto> changePassword(@RequestBody String oldPassword, String newPassword) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            userService.changePassword(email, oldPassword, newPassword);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "비밀번호 초기화 링크 이메일 전송", description = "사용자의 이메일로 비밀번호 초기화 링크를 전송합니다.")
    @PostMapping("/send-reset-email")
    public ResponseEntity<UserInfoResponseDto> sendResetEmail(@RequestBody UserResetPasswordRequestDto requestDto) {
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
            @ApiResponse(responseCode = "200", description = "유저 정보 반환", content = @Content(schema = @Schema(implementation = JwtResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "유저가 로그인하지 않았거나 정보가 없을 시 반환")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<JwtResponseDto> resetPassword(@RequestBody String password) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            JwtResponseDto responseDto = userService.resetPassword(email, password);
            return ResponseEntity.ok(responseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> resetPassword() {
        try {
            mailService.sendResetPassword("tizmfns1218@naver.com");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("메일 오류 발생" + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    // TODO 유저의 자원봉사 내역


}
