package com.study.dawheen.user.service;

import com.study.dawheen.auth.jwt.JwtService;
import com.study.dawheen.common.dto.TokenResponseDto;
import com.study.dawheen.config.TestSecurityConfig;
import com.study.dawheen.infra.mail.MailService;
import com.study.dawheen.user.dto.UserCreateRequestDto;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    private UserCreateRequestDto userCreateRequestDto;
    private User user;

    @BeforeEach
    void setUp() {
        // Mock the encode method
        when(passwordEncoder.encode(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String rawPassword = invocation.getArgument(0);
                    String encodedPassword = "encoded_" + rawPassword;
                    return String.format("%1$-" + 60 + "s", encodedPassword).replace(' ', 'x');
                });
        userCreateRequestDto = new UserCreateRequestDto("user1@gmail.com", "1234", "user");
        // Use the mocked encoder for user creation
        user = User.builder()
                .email(userCreateRequestDto.getEmail())
                .password(passwordEncoder.encode(userCreateRequestDto.getPassword())) // Ensure password matches the mock behavior
                .name(userCreateRequestDto.getName())
                .roleType(RoleType.MEMBER)
                .build();
    }

    @Test
    @DisplayName("계정 생성 테스트")
    void createUser() {
        // given
        given(jwtService.createAccessToken(anyString())).willReturn("accessToken");
        given(jwtService.createRefreshToken()).willReturn("refreshToken");
        given(userRepository.save(any())).willReturn(user);
        // when
        TokenResponseDto responseDto = userService.createUser(userCreateRequestDto);

        // then
        verify(userRepository).save(any());

        assertThat(responseDto).isEqualTo(new TokenResponseDto("accessToken", "refreshToken"));
    }

    @Test
    @DisplayName("계정 검색 테스트")
    void getUser() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // when
        UserInfoResponseDto responseDto = userService.getUser("user1@gmail.com");

        // then
        verify(userRepository).findByEmail(any());

        assertThat(responseDto).isEqualTo(new UserInfoResponseDto(user));
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void changePassword() {
        // Given
        String email = "user@example.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        User user1 = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        when(userService.equalPassword(user1.getPassword(), oldPassword)).thenReturn(true);

        userService.changePassword(email, oldPassword, newPassword);

        // Then
        verify(user1).changePassword(userService.encodePassword(newPassword));
    }

    @Test
    @DisplayName("비밀번호 동일 변경 테스트")
    void changeSamePassword() {
        // Given
        String email = "user@example.com";
        String oldPassword = "password";
        String newPassword = "password";

        User user1 = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        when(userService.equalPassword(user1.getPassword(), oldPassword)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> {
            userService.changePassword(email, oldPassword, newPassword);
        });

        verify(user1, never()).changePassword(anyString());
    }

    @Test
    @DisplayName("계정 검색 - 존재하지 않는 사용자일 때 예외 발생 테스트")
    void getUser_whenUserNotFound_thenThrowException() {
        // given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUser("nonexistentuser@gmail.com"));

        // verify that the repository method was called
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    @DisplayName("비밀번호 재설정 테스트")
    void resetPasswordTest() {
        // Given
        String email = "user@example.com";
        String newPassword = "newPassword123";


        User user1 = mock(User.class);
        String accessToken = "newAccessToken";
        String refreshToken = "newRefreshToken";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        when(jwtService.createAccessToken(email)).thenReturn(accessToken);
        when(jwtService.createRefreshToken()).thenReturn(refreshToken);

        // When
        TokenResponseDto response = userService.resetPassword(email, newPassword);

        // Then
        verify(userRepository).findByEmail(email);
        verify(user1).changePassword(userService.encodePassword(newPassword));
        verify(jwtService).createAccessToken(email);
        verify(jwtService).createRefreshToken();
        verify(jwtService).saveRefreshToken(refreshToken, email);

        Assertions.assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }

}

