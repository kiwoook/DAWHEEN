package com.study.dawheen.config;


import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepository;
import com.study.dawheen.volunteer.service.VolunteerRankingService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class RedisTest {
    private static final String REDIS_IMAGE = "redis:6.0.20";

    private static final int REDIS_PORT = 6379;
    private static final GenericContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);
        REDIS_CONTAINER.start();
    }

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    UserVolunteerRepository userVolunteerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VolunteerWorkRepository volunteerWorkRepository;

    @Autowired
    VolunteerRankingService volunteerRankingService;
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

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT)
                .toString());
    }
    @Disabled
    @Test
    @DisplayName("Redis GET / SET 테스트")
    void getRedisTest() {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();

        values.set("ranking", "hello", 1, TimeUnit.DAYS);
        String value = (String) values.get("ranking");
        assertThat(value).isEqualTo("hello");
    }

    @Disabled
    @Test
    @DisplayName("랭킹 Fetch 후 데이터 가져오기")
    void fetchRanking() throws IOException {
        // given
        User[] users = {user1, user2, user3, user4, user5};
        int max_value = 1;

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
            max_value += 1;
        }

        List<UserInfoResponseDto> expectedResponseDtoList = new ArrayList<>();
        expectedResponseDtoList.add(new UserInfoResponseDto(user5));
        expectedResponseDtoList.add(new UserInfoResponseDto(user4));
        expectedResponseDtoList.add(new UserInfoResponseDto(user3));
        expectedResponseDtoList.add(new UserInfoResponseDto(user2));
        expectedResponseDtoList.add(new UserInfoResponseDto(user1));


        volunteerRankingService.fetchMonthlyRankingToRedis();
        List<UserInfoResponseDto> actualResponseDtoList = volunteerRankingService.getMonthlyRanking();

        //then
        assertThat(actualResponseDtoList)
                .isEqualTo(expectedResponseDtoList);
    }
}
