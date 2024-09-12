package com.study.dawheen.volunteer.repository;

import com.study.dawheen.chat.repository.ChatMessageRepository;
import com.study.dawheen.custom.JpaRepositoryTest;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.repository.OrganRepository;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerUserRankingDto;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(OutputCaptureExtension.class)
@JpaRepositoryTest
class VolunteerRankingJpaRepositoryTest {
    static final int NUM_USERS = 50; // 생성할 사용자 수

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

    private Organization organization;

    @Autowired
    private OrganRepository organRepository;

    @BeforeEach
    void setup() {
        organization = Organization.builder()
                .name("Test Organization")
                .facilityPhone("02-1234-5678")
                .email("test@organization.com")
                .facilityType("Hospital")
                .representName("John Doe")
                .build();

        organRepository.save(organization);

        when(passwordEncoder.encode(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    String rawPassword = invocation.getArgument(0);
                    String encodedPassword = "encoded_" + rawPassword;
                    return String.format("%1$-" + 60 + "s", encodedPassword).replace(' ', 'x');
                });

        List<User> users = new ArrayList<>();
        IntStream.range(1, NUM_USERS + 1).forEach(i -> {
            String email = "test" + i + "@gmail.com";
            User user = User.builder()
                    .name("user" + i)
                    .email(email)
                    .password(passwordEncoder.encode("1234"))
                    .roleType(RoleType.MEMBER)
                    .build();
            users.add(user);
        });
        // 한 5개정도 만들자...
        int max_value = 10;

        for (User user : users) {
            for (int i = 0; i < max_value; i++) {
                VolunteerWork volunteerWork = VolunteerWork.builder()
                        .organization(organization)
                        .title("Sample Volunteer Work")
                        .content("This is a sample content.")
                        .serviceStartDatetime(LocalDateTime.of(2024, 1, 1, 9, 0))
                        .serviceEndDatetime(LocalDateTime.of(2024, 12, 31, 17, 0))
                        .serviceDays(Set.of(LocalDate.now().getDayOfWeek()))
                        .targetAudiences(Set.of(TargetAudience.ANIMAL))
                        .volunteerTypes(Set.of(VolunteerType.ADULT))
                        .recruitStartDateTime(LocalDateTime.now())
                        .recruitEndDateTime(LocalDateTime.now().plusMonths(1))
                        .maxParticipants(1)
                        .build();
                volunteerWorkRepository.save(volunteerWork);
                userRepository.save(user);
                UserVolunteerWork mockUserVolunteerWork = new UserVolunteerWork(user, volunteerWork);
                mockUserVolunteerWork.updateStatus(ApplyStatus.COMPLETED);
                userVolunteerRepository.save(mockUserVolunteerWork);
            }
            max_value += 10;
        }

    }

    //

    @Order(1)
    @Test
    @DisplayName("월간 랭킹 테스트")
    void monthlyRankingTest() {
        //given
        String[] answer = {"user50", "user49", "user48", "user47", "user46"};
        //when
        List<UserInfoResponseDto> responseDtoList = userVolunteerRepository.getVolunteerActivityRankings(LocalDateTime.now().minusMonths(1));

        //then
        assertThat(responseDtoList).hasSize(20);

        for (int i = 0; i < 5; i++) {
            assertThat(responseDtoList.get(i).getName()).isEqualTo(answer[i]);
        }
    }

    @Order(3)
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

        VolunteerWork volunteerWork = VolunteerWork.builder()
                .organization(organization)
                .title("Sample Volunteer Work")
                .content("This is a sample content.")
                .serviceStartDatetime(LocalDateTime.of(2024, 1, 1, 9, 0))
                .serviceEndDatetime(LocalDateTime.of(2024, 12, 31, 17, 0))
                .serviceDays(Set.of(LocalDate.now().getDayOfWeek()))
                .targetAudiences(Set.of(TargetAudience.ANIMAL))
                .volunteerTypes(Set.of(VolunteerType.ADULT))
                .recruitStartDateTime(LocalDateTime.now())
                .recruitEndDateTime(LocalDateTime.now().plusMonths(1))
                .maxParticipants(1)
                .build();

        volunteerWorkRepository.save(volunteerWork);
        UserVolunteerWork mockUserVolunteerWork = new UserVolunteerWork(user6, volunteerWork);
        userVolunteerRepository.save(mockUserVolunteerWork);

        //when
        List<UserInfoResponseDto> responseDtoList = userVolunteerRepository.getVolunteerActivityRankings(LocalDateTime.now().minusMonths(1));

        //then
        assertThat(responseDtoList).hasSize(20);
    }

    @Order(2)
    @Test
    @DisplayName("기간 별 봉사활동 개수 확인 테스트")
    void getUserVolunteerCountByPeriodTest() {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDateTime = LocalDateTime.now();

        // when
        List<VolunteerUserRankingDto> responseDtoList = userVolunteerRepository.getUserVolunteerCountByPeriod(startDateTime, endDateTime);

        // then
        assertThat(responseDtoList).hasSize(NUM_USERS);

//        assertThat(responseDtoList).extracting("userEmail")
//                .containsExactlyInAnyOrder("user1@gmail.com", "user2@gmail.com", "user3@gmail.com", "user4@gmail.com", "user5@gmail.com");
//
//        assertThat(responseDtoList).extracting("count")
//                .containsExactlyInAnyOrder(15L, 20L, 25L, 30L, 35L); // 각 유저의 봉사활동 개수는 max_value에 따라 다릅니다.
    }

    @AfterEach
    void cleanUp(){
        userRepository.deleteAll();
        organRepository.deleteAll();
        volunteerWorkRepository.deleteAll();
    }
}
