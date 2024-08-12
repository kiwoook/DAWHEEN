package com.study.dawheen.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dawheen.chat.repository.ChatMessageRepository;
import com.study.dawheen.user.dto.UserPasswordChangeRequestDto;
import com.study.dawheen.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private static final String API_URL = "/api/v1/user";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvcAutoConfiguration mockMvcAutoConfiguration;
    @MockBean
    private UserService userService;
    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @Test
    @WithMockUser(username = "user@example.com", roles = "MEMBER")
    @DisplayName("비밀번호 변경 성공 테스트")
    void changePasswordSuccess() throws Exception {
        // Given
        UserPasswordChangeRequestDto requestDto = new UserPasswordChangeRequestDto("oldPassword", "newPassword");
        doNothing().when(userService).changePassword(anyString(), eq("oldPassword"), eq("newPassword"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))  // CSRF 토큰 추가
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify userService.changePassword() was called with correct arguments
        verify(userService, times(1)).changePassword("user@example.com", "oldPassword", "newPassword");
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "MEMBER")
    @DisplayName("비밀번호 변경 실패 테스트 - 유저 미존재")
    void changePasswordUserNotFound() throws Exception {
        // Given
        UserPasswordChangeRequestDto requestDto = new UserPasswordChangeRequestDto("oldPassword", "newPassword");

        // Set up mock behavior
        doThrow(new EntityNotFoundException()).when(userService).changePassword(anyString(), anyString(), anyString());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf())  // CSRF 토큰 추가
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // Verify userService.changePassword() was called with correct arguments
        verify(userService, times(1)).changePassword("user@example.com", "oldPassword", "newPassword");
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "MEMBER")
    @DisplayName("비밀번호 변경 실패 테스트 - 비밀번호 불일치")
    void changePasswordOldPasswordMismatch() throws Exception {
        // Given
        UserPasswordChangeRequestDto requestDto = new UserPasswordChangeRequestDto("wrongOldPassword", "newPassword");

        // Set up mock behavior
        doThrow(new IllegalStateException()).when(userService).changePassword(anyString(), anyString(), anyString());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Verify userService.changePassword() was called with correct arguments
        verify(userService, times(1)).changePassword("user@example.com", "wrongOldPassword", "newPassword");
    }
}

