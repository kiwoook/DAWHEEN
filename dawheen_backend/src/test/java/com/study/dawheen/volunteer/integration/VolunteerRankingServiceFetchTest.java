package com.study.dawheen.volunteer.integration;

import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.RoleType;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.dto.VolunteerUserRankingDto;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.service.RankingProcessService;
import com.study.dawheen.volunteer.service.impl.VolunteerRankingServiceV2;
import jakarta.persistence.EntityNotFoundException;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.study.dawheen.volunteer.service.RankingConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@EnableRetry
@ExtendWith({OutputCaptureExtension.class, SpringExtension.class})
class VolunteerRankingServiceFetchTest {

    private static final Logger log = LoggerFactory.getLogger(VolunteerRankingServiceFetchTest.class);

    private static final String REDIS_IMAGE = "redis:6.0.20";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer(REDIS_IMAGE).withExposedPorts(REDIS_PORT).withReuse(true);
        REDIS_CONTAINER.start();
    }

    @MockBean
    private UserVolunteerRepository userVolunteerRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private VolunteerRankingServiceV2 volunteerRankingServiceV2;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RankingProcessService rankingProcessService;

    @Autowired
    private UserRepository userRepository;

    private List<UserInfoResponseDto> resultResponseDto;

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT).toString());
    }

    @BeforeEach
    void setUp() {
        // 유저 생성
        List<User> userList = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            userList.add(User.builder()
                    .email("user" + i + "@gmail.com")
                    .password(passwordEncoder.encode("1234"))
                    .name("user" + i)
                    .roleType(RoleType.MEMBER)
                    .build());
        }

        resultResponseDto = userList.stream().map(UserInfoResponseDto::new).limit(20).toList();

        userRepository.saveAll(userList);

        // dto 생성
        List<VolunteerUserRankingDto> mockRankingList = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            String email = "user" + i + "@gmail.com";
            double count = 31 - i;  // 숫자가 작을수록 count 값이 큼
            mockRankingList.add(new VolunteerUserRankingDto(email, count));
        }

        when(userVolunteerRepository.getUserVolunteerCountByPeriod(any(), any()))
                .thenReturn(mockRankingList);

        volunteerRankingServiceV2.fetchRankingData();

    }

    @DisplayName("월간 랭킹 성공")
    @Test
    void testGetMonthlyRanking() throws IOException, InterruptedException {
        // given

        // when
        List<UserInfoResponseDto> responseDtoList = volunteerRankingServiceV2.getMonthlyRanking();

        // then
        assertThat(responseDtoList).isEqualTo(resultResponseDto);

    }

    @DisplayName("월간 랭킹 동시성 테스트")
    @Test
    void testGetMonthlyRanking_Concurrency() throws InterruptedException, ExecutionException {
        int numberOfThreads = 100;  // 동시 실행할 스레드 수

        try (ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads)) {
            Callable<List<UserInfoResponseDto>> task = () -> volunteerRankingServiceV2.getMonthlyRanking();

            List<Future<List<UserInfoResponseDto>>> futures = new ArrayList<>();

            for (int i = 0; i < numberOfThreads; i++) {
                futures.add(executorService.submit(task));
            }

            // 모든 스레드의 작업 결과를 확인
            for (Future<List<UserInfoResponseDto>> future : futures) {
                List<UserInfoResponseDto> result = future.get();
                assertThat(result).isNotNull();  // 결과가 null이 아닌지 확인
                assertThat(result).hasSizeGreaterThan(0);  // 결과의 크기 확인
            }
        }

    }

    @DisplayName("월간 랭킹 실패 : Empty Cache")
    @Test
    void testGetMonthlyRankingWithEmptyCache1() {
        // given

        // MONTHLY_RANKING 캐시 지우기
        redisTemplate.delete(MONTHLY_RANKING);

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            volunteerRankingServiceV2.getMonthlyRanking();
        });
    }


    // TODO 재시도 왜 안됨?
    @Disabled
    @DisplayName("월간 랭킹 실패 : Empty 캐시 후 재시도")
    @Test
    void testGetMonthlyRankingWithEmptyCache2() throws IOException, InterruptedException {
        // given

        // MONTHLY_RANKING 캐시 지우기
        redisTemplate.delete(MONTHLY_RANKING);

        // when & then
        List<UserInfoResponseDto> result = null;
        result = volunteerRankingServiceV2.getMonthlyRanking();

        // 값이 정상적으로 반환되었는지 확인
        assertThat(result).isEqualTo(resultResponseDto);

    }

    @DisplayName("반년간 랭킹 성공")
    @Test
    void testGetSemiAnnualRanking() throws IOException, InterruptedException {

        // given
        // when
        List<UserInfoResponseDto> responseDtoList = volunteerRankingServiceV2.getSemiAnnualRanking();

        // then
        assertThat(responseDtoList).isEqualTo(resultResponseDto);

    }

    @DisplayName("년간 랭킹 성공")
    @Test
    void testGetAnnualRanking() throws IOException, InterruptedException {

        // given
        // when
        List<UserInfoResponseDto> responseDtoList = volunteerRankingServiceV2.getAnnualRanking();

        // then
        assertThat(responseDtoList).isEqualTo(resultResponseDto);

    }

    @DisplayName("대규모 데이터 처리 성능 테스트")
    @Test
    void testLargeScaleDataProcessing() throws IOException, InterruptedException {
        // given

        // 초기 세팅 없애기
        userRepository.deleteAll();

        int numberOfUsers = 100; // 대규모 데이터 설정
        List<User> userList = new ArrayList<>();

        for (int i = 1; i <= numberOfUsers; i++) {
            userList.add(User.builder()
                    .email("user" + i + "@gmail.com")
                    .password(passwordEncoder.encode("1234"))
                    .name("user" + i)
                    .roleType(RoleType.MEMBER)
                    .build());
        }

        userRepository.saveAll(userList);

        List<VolunteerUserRankingDto> mockRankingList = new ArrayList<>();
        for (int i = 1; i <= numberOfUsers; i++) {
            String email = "user" + i + "@gmail.com";
            double count = numberOfUsers - i + 1;
            mockRankingList.add(new VolunteerUserRankingDto(email, count));
        }

        when(userVolunteerRepository.getUserVolunteerCountByPeriod(any(), any()))
                .thenReturn(mockRankingList);

        // when
        LocalDateTime startDateTime = LocalDateTime.now();

        volunteerRankingServiceV2.fetchRankingData();

        List<UserInfoResponseDto> responseDtoList = volunteerRankingServiceV2.getMonthlyRanking();

        LocalDateTime endDateTime = LocalDateTime.now();

        log.info("시작 시간 = {}, 걸린 시간 = {}", startDateTime, endDateTime);
        // then
        assertThat(responseDtoList).isEqualTo(resultResponseDto);
    }


    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        redisTemplate.delete(MONTHLY_RANKING);
        redisTemplate.delete(SEMI_ANNUAL_RANKING);
        redisTemplate.delete(ANNUAL_RANKING);

        redisTemplate.delete(MONTHLY_RANKING_USER_INFO);
        redisTemplate.delete(SEMI_ANNUAL_RANKING_USER_INFO);
        redisTemplate.delete(ANNUAL_RANKING_USER_INFO);
    }

}
