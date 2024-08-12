package com.study.dawheen.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dawheen.user.controller.UserController;
import com.study.dawheen.user.dto.UserPasswordChangeRequestDto;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ExtendWith(OutputCaptureExtension.class)
class UserControllerIntegrationTest {

    private static final String API_URL = "/api/v1/user";
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 테스트용 유저 데이터를 저장
        User user = User.builder()
                .email("user@example.com")
                .password(userService.encodePassword("oldPassword"))
                .name("name")
                .roleType(RoleType.MEMBER)
                .build();
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "MEMBER")
    @DisplayName("비밀번호 변경 통합 테스트")
    void changePasswordSuccess() throws Exception {
        // Given
        UserPasswordChangeRequestDto requestDto = new UserPasswordChangeRequestDto("oldPassword", "newPassword");
        log.info("JSON : {}", objectMapper.writeValueAsString(requestDto));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify: 비밀번호가 실제로 변경되었는지 확인
        User user = userRepository.findByEmail("user@example.com").orElseThrow(EntityNotFoundException::new);
        log.info("User : {}", user);
        boolean passwordMatches = passwordEncoder.matches("newPassword", user.getPassword());
        assertTrue(passwordMatches, "Password should be updated successfully");
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "MEMBER")
    @DisplayName("잘못된 기존 비밀번호로 비밀번호 변경 실패 테스트")
    void changePasswordFailureDueToIncorrectOldPassword() throws Exception {
        // Given
        UserPasswordChangeRequestDto requestDto = new UserPasswordChangeRequestDto("wrongOldPassword", "newPassword");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
}
