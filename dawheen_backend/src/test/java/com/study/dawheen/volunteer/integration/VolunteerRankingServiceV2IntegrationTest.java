package com.study.dawheen.volunteer.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dawheen.volunteer.dto.VolunteerUserRankingDto;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.service.impl.RankingProcessServiceImpl;
import com.study.dawheen.volunteer.service.impl.VolunteerRankingServiceV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class VolunteerRankingServiceV2IntegrationTest {
    private static final String REDIS_IMAGE = "redis:6.0.20";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer<?> REDIS_CONTAINER;
    private static final String MONTHLY_RANKING = "monthly-ranking";
    private static final String SEMI_ANNUAL_RANKING = "semi-annual-ranking";
    private static final String ANNUAL_RANKING = "annual-ranking";
    private static final String MONTHLY_RANKING_USER_INFO = "monthly-ranking-user-info";
    private static final String SEMI_ANNUAL_RANKING_USER_INFO = "semi-annual-ranking-user-info";
    private static final String ANNUAL_RANKING_USER_INFO = "annual-ranking-user-info";

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
    private RankingProcessServiceImpl rankingProcessService;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT).toString());
    }


    @Test
    @DisplayName("레디스 동작 테스트")
    void testRedisIntegration() {
        // Given
        ValueOperations<String, Object> values = redisTemplate.opsForValue();

        // When
        values.set("testKey", "testValue", 1, TimeUnit.HOURS);

        // Then
        String result = (String) values.get("testKey");
        assertThat(result).isEqualTo("testValue");
    }

    @Test
    @DisplayName("addVolunteerUser 테스트")
    void testAddVolunteerUser() {
        // Given
        String email = "user1@example.com";

        // When

        rankingProcessService.incrementRankingScores(email);

        // Then
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        Double monthlyScore = zSetOperations.score(MONTHLY_RANKING, email);
        Double semiAnnualScore = zSetOperations.score(SEMI_ANNUAL_RANKING, email);
        Double annualScore = zSetOperations.score(ANNUAL_RANKING, email);

        assertThat(monthlyScore).isEqualTo(1);
        assertThat(semiAnnualScore).isEqualTo(1);
        assertThat(annualScore).isEqualTo(1);


    }

    @Test
    @DisplayName("fetchRankingData 테스트")
    void testFetchRankingData() {

        // Given
        List<VolunteerUserRankingDto> monthlyRanking = List.of(
                new VolunteerUserRankingDto("user1@example.com", 5d),
                new VolunteerUserRankingDto("user2@example.com", 3d)
        );
        List<VolunteerUserRankingDto> semiAnnualRanking = List.of(
                new VolunteerUserRankingDto("user1@example.com", 10d),
                new VolunteerUserRankingDto("user2@example.com", 8d)
        );
        List<VolunteerUserRankingDto> annualRanking = List.of(
                new VolunteerUserRankingDto("user1@example.com", 20d),
                new VolunteerUserRankingDto("user2@example.com", 15d)
        );

        // Mock repository calls
        when(userVolunteerRepository.getUserVolunteerCountByPeriod(any(), any()))
                .thenReturn(monthlyRanking)
                .thenReturn(semiAnnualRanking)
                .thenReturn(annualRanking);

        // When
        volunteerRankingServiceV2.fetchRankingData();

        // Then
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

        Double monthlyScoreUser1 = zSetOperations.score(MONTHLY_RANKING, "user1@example.com");
        Double semiAnnualScoreUser1 = zSetOperations.score(SEMI_ANNUAL_RANKING, "user1@example.com");
        Double annualScoreUser1 = zSetOperations.score(ANNUAL_RANKING, "user1@example.com");
        Double monthlyScoreUser2 = zSetOperations.score(MONTHLY_RANKING, "user2@example.com");
        Double semiAnnualScoreUser2 = zSetOperations.score(SEMI_ANNUAL_RANKING, "user2@example.com");
        Double annualScoreUser2 = zSetOperations.score(ANNUAL_RANKING, "user2@example.com");

        assertThat(monthlyScoreUser1).isEqualTo(5d);
        assertThat(semiAnnualScoreUser1).isEqualTo(15d);
        assertThat(annualScoreUser1).isEqualTo(35d);

        assertThat(monthlyScoreUser2).isEqualTo(3d);
        assertThat(semiAnnualScoreUser2).isEqualTo(11d);
        assertThat(annualScoreUser2).isEqualTo(26d);
    }


    @AfterEach
    void cleanUp() {
        redisTemplate.delete(MONTHLY_RANKING);
        redisTemplate.delete(SEMI_ANNUAL_RANKING);
        redisTemplate.delete(ANNUAL_RANKING);

        redisTemplate.delete(MONTHLY_RANKING_USER_INFO);
        redisTemplate.delete(SEMI_ANNUAL_RANKING_USER_INFO);
        redisTemplate.delete(ANNUAL_RANKING_USER_INFO);
    }

}
