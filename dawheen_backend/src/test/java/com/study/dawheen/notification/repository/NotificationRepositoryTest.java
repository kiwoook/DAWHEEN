package com.study.dawheen.notification.repository;

import com.study.dawheen.notification.entity.Notification;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotificationRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(NotificationRepositoryTest.class);
    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    User user1 = User.builder()
            .name("user1")
            .email("user1@gmail.com")
            .password("password")
            .build();

    @BeforeEach
    void setup() {
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
