package com.study.dawheen.volunteer.service;

import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class VolunteerRankingServiceTest {

    @Mock
    UserVolunteerRepository userVolunteerRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    VolunteerWorkRepository volunteerWorkRepository;

    @Mock
    RedisTemplate<String, Object> redisTemplate;
    User user1 = User.builder()
            .name("user1")
            .email("user1@gmail.com")
            .password("password")
            .build();

    // 엔티티를 저장하고 Redis 에서 동작하는 지 확인해야한다.
    User user2 = User.builder()
            .name("user2")
            .email("user2@gmail.com")
            .password("password")
            .build();
    User user3 = User.builder()
            .name("user3")
            .email("user3@gmail.com")
            .password("password")
            .build();
    User user4 = User.builder()
            .name("user4")
            .email("user4@gmail.com")
            .password("password")
            .build();
    User user5 = User.builder()
            .name("user5")
            .email("user5@gmail.com")
            .password("password")
            .build();
    @InjectMocks
    private VolunteerRankingService volunteerRankingService;

    @Nested
    @DisplayName("랭킹 동작 후 조회 확인")
    class FetchAndCheck {

        @BeforeEach
        void setup() {
            //given

            User[] users = {user1, user2, user3, user4, user5};
            int max_value = 15;

            for (User user : users) {
                for (int i = 0; i < max_value; i++) {
                    VolunteerWork volunteerWork = VolunteerWork.builder()
                            .build();
                    given(volunteerWorkRepository.save(any(VolunteerWork.class))).willReturn(volunteerWork);
                    given(userRepository.save(user)).willReturn(user);
                    UserVolunteerWork mockUserVolunteerWork = new UserVolunteerWork(user, volunteerWork);
                    mockUserVolunteerWork.updateStatus(ApplyStatus.COMPLETED);
                    given(userVolunteerRepository.save(any(UserVolunteerWork.class))).willReturn(mockUserVolunteerWork);
                }
                max_value += 5;
            }
        }

        @Disabled
        @Test
        @DisplayName("랭킹 Fetch 후 데이터 가져오기")
        void fetchRanking() throws IOException {
            //given
            List<UserInfoResponseDto> expectedResponseDtoList = new ArrayList<>();
            expectedResponseDtoList.add(new UserInfoResponseDto(user5));
            expectedResponseDtoList.add(new UserInfoResponseDto(user4));
            expectedResponseDtoList.add(new UserInfoResponseDto(user3));
            expectedResponseDtoList.add(new UserInfoResponseDto(user2));
            expectedResponseDtoList.add(new UserInfoResponseDto(user1));

            given(userVolunteerRepository.getMonthlyVolunteerActivityRankings()).willReturn(expectedResponseDtoList);

            //when
            volunteerRankingService.fetchMonthlyRankingToRedis();
            List<UserInfoResponseDto> actualResponseDtoList = volunteerRankingService.getMonthlyRanking();

            //then
            assertThat(actualResponseDtoList)
                    .isEqualTo(expectedResponseDtoList);
            verify(redisTemplate, times(1)).opsForValue();

        }
}
}
