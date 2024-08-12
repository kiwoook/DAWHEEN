package com.study.dawheen.chat.repository;

import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;


@DataJpaTest
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatRoomUserRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomRepository.class);

    @MockBean
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    ChatRoomUserRepository chatRoomUserRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {

        // 유저 생성

        User user1 = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .password("password")
                .roleType(RoleType.MEMBER)
                .build();

        userRepository.save(user1);

        // 자원 봉사 생성되면서
        VolunteerWork volunteerWork1 = VolunteerWork.builder()
                .build();
        VolunteerWork volunteerWork2 = VolunteerWork.builder()
                .build();
        VolunteerWork volunteerWork3 = VolunteerWork.builder()
                .build();
        VolunteerWork volunteerWork4 = VolunteerWork.builder()
                .build();
        VolunteerWork volunteerWork5 = VolunteerWork.builder()
                .build();



    }

}
