package com.study.dawheen.notification.repository;

import com.study.dawheen.chat.repository.ChatMessageRepository;
import com.study.dawheen.notification.entity.Notification;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DataJpaTest
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class NotificationJpaRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(NotificationJpaRepositoryTest.class);

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;
    User user1;
    @Mock
    private PasswordEncoder passwordEncoder;
    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setup() {

        when(passwordEncoder.encode(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String rawPassword = invocation.getArgument(0);
                    String encodedPassword = "encoded_" + rawPassword;
                    return String.format("%1$-" + 60 + "s", encodedPassword).replace(' ', 'x');
                });

        user1 = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .password(passwordEncoder.encode("password"))
                .roleType(RoleType.MEMBER)
                .build();

        userRepository.save(user1);
    }

    @Test
    @DisplayName("알림 ID, 이메일 확인 및 N+1 문제 테스트")
    void findNotificationByIdAndEmail() {
        Notification hello = Notification.builder().receiver(user1).content("hello").build();
        notificationRepository.save(hello);

        Notification notification = notificationRepository.findByIdAndReceiverEmail(1L, "user1@gmail.com").orElseThrow(EntityNotFoundException::new);

        log.info("해당 ID = {}, 내용 = {}", notification.getId(), notification.getContent());
        assertThat(notification).isEqualTo(hello);
    }
}
