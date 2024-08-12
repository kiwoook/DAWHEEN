package com.study.dawheen.volunteer.repository;

import com.study.dawheen.chat.repository.ChatMessageRepository;
import com.study.dawheen.config.TestSecurityConfig;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@DataJpaTest
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestSecurityConfig.class)
class VolunteerRankingRepositoryTest {

    @Autowired
    UserVolunteerRepository userVolunteerRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    VolunteerWorkRepository volunteerWorkRepository;
    @MockBean
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setup() {

        when(passwordEncoder.encode(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String rawPassword = invocation.getArgument(0);
                    String encodedPassword = "encoded_" + rawPassword;
                    return String.format("%1$-" + 60 + "s", encodedPassword).replace(' ', 'x');
                });
        String rawPassword = "password";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user1 = User.builder()
                .name("user1")
                .email("user1@gmail.com")
                .password(encodedPassword)
                .roleType(RoleType.MEMBER)
                .build();

        User user2 = User.builder()
                .name("user2")
                .email("user2@gmail.com")
                .password(encodedPassword)
                .roleType(RoleType.MEMBER)
                .build();

        User user3 = User.builder()
                .name("user3")
                .email("user3@gmail.com")
                .password(encodedPassword)
                .roleType(RoleType.MEMBER)
                .build();

        User user4 = User.builder()
                .name("user4")
                .email("user4@gmail.com")
                .password(encodedPassword)
                .roleType(RoleType.MEMBER)
                .build();

        User user5 = User.builder()
                .name("user5")
                .email("user5@gmail.com")
                .password(encodedPassword)
                .roleType(RoleType.MEMBER)
                .build();

        User[] users = {user1, user2, user3, user4, user5};
        // 한 5개정도 만들자...
        int max_value = 15;

        for (User user : users) {
            for (int i = 0; i < max_value; i++) {
                VolunteerWork volunteerWork = VolunteerWork.builder()
                        .build();
                volunteerWorkRepository.save(volunteerWork);
                userRepository.save(user);
                UserVolunteerWork mockUserVolunteerWork = new UserVolunteerWork(user, volunteerWork);
                mockUserVolunteerWork.updateStatus(ApplyStatus.COMPLETED);
                userVolunteerRepository.save(mockUserVolunteerWork);
            }
            max_value += 5;
        }

    }

    //

    @Order(1)
    @Test
    @DisplayName("월간 랭킹 테스트")
    void monthlyRankingTest() {
        //given
        String[] answer = {"user5", "user4", "user3", "user2", "user1"};
        //when
        List<UserInfoResponseDto> responseDtoList = userVolunteerRepository.getMonthlyVolunteerActivityRankings();

        //then
        assertThat(responseDtoList).hasSize(5);

        for (int i = 0; i < 5; i++) {
            assertThat(responseDtoList.get(i).getName()).isEqualTo(answer[i]);
        }
    }

    @Order(2)
    @Test
    @DisplayName("완료 status 확인 테스트")
    void monthlyRankingStatusTest() {
        //given

        User user6 = User.builder()
                .name("user6")
                .email("user6@gmail.com")
                .password(passwordEncoder.encode("password"))
                .roleType(RoleType.MEMBER)

                .build();
        userRepository.save(user6);

        VolunteerWork volunteerWork = VolunteerWork.builder().build();
        volunteerWorkRepository.save(volunteerWork);
        UserVolunteerWork mockUserVolunteerWork = new UserVolunteerWork(user6, volunteerWork);
        userVolunteerRepository.save(mockUserVolunteerWork);

        //when
        List<UserInfoResponseDto> responseDtoList = userVolunteerRepository.getMonthlyVolunteerActivityRankings();

        //then
        assertThat(responseDtoList).hasSize(5);
    }

}
