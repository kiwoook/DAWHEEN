package com.study.dawheen.volunteer.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.repository.UserRepository;
import com.study.dawheen.volunteer.service.RankingProcessService;
import com.study.dawheen.volunteer.service.VolunteerRankingService;
import io.lettuce.core.RedisException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.study.dawheen.volunteer.service.RankingConstants.*;

@Service
@Slf4j
public class VolunteerRankingServiceV2 implements VolunteerRankingService {


    private final UserRepository userRepository;
    private final RankingProcessService rankingProcessService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ValueOperations<String, Object> values;

    public VolunteerRankingServiceV2(UserRepository userRepository, RankingProcessService rankingProcessService, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.rankingProcessService = rankingProcessService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.values = redisTemplate.opsForValue();
    }

    @Retryable(
            retryFor = {RedisSystemException.class},
            backoff = @Backoff(delay = 2000)
    )
    @Override
    public void addVolunteerUser(String email) throws JsonProcessingException {
        // 각 key 에 추가한다.

        rankingProcessService.incrementRankingScores(email);

        fetchUserInfo(MONTHLY_RANKING, MONTHLY_RANKING_USER_INFO);
        fetchUserInfo(SEMI_ANNUAL_RANKING, SEMI_ANNUAL_RANKING_USER_INFO);
        fetchUserInfo(ANNUAL_RANKING, ANNUAL_RANKING_USER_INFO);
    }

    // 정합성을 맞추기 위해 매일 정각마다 재설정
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional(readOnly = true)
    @Override
    public void fetchRankingData() {
        log.info("fetchRankingData 실행");

        rankingProcessService.refreshRankingData();

        // 이후 작업 수행
        redisTemplate.expire(MONTHLY_RANKING, 25, TimeUnit.HOURS);
        redisTemplate.expire(SEMI_ANNUAL_RANKING, 25, TimeUnit.HOURS);
        redisTemplate.expire(ANNUAL_RANKING, 25, TimeUnit.HOURS);

    }

    @Override
    @Transactional(readOnly = true)
    public List<UserInfoResponseDto> getMonthlyRanking() throws IOException, EntityNotFoundException, InterruptedException {
        return this.getRanking(MONTHLY_RANKING, MONTHLY_RANKING_USER_INFO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserInfoResponseDto> getSemiAnnualRanking() throws IOException, EntityNotFoundException, InterruptedException {
        return getRanking(SEMI_ANNUAL_RANKING, SEMI_ANNUAL_RANKING_USER_INFO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserInfoResponseDto> getAnnualRanking() throws IOException, EntityNotFoundException, InterruptedException {
        return getRanking(ANNUAL_RANKING, ANNUAL_RANKING_USER_INFO);
    }

    @Retryable(
            retryFor = RedisException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000)
    )
    @Synchronized
    private List<UserInfoResponseDto> getRanking(String rankingKey, String userInfoKey) throws IOException {
        log.info("getRanking 호출");

        if (values.get(userInfoKey) == null) {
            fetchUserInfo(rankingKey, userInfoKey);
        }

        return deserializeUserResponseDtoList((String) values.get(userInfoKey));
    }

    @Retryable(
            backoff = @Backoff(delay = 2000),
            retryFor = {EntityNotFoundException.class, RedisException.class}
    )
    public void fetchUserInfo(String rankingKey, String userInfoKey) throws JsonProcessingException, EntityNotFoundException {
        String lockKey = LOCK_KEY_PREFIX + rankingKey;

        // 비관적 락을 시도합니다.
        try {
            boolean lockAcquired = acquireLock(lockKey, 5);

            if (!lockAcquired) {
                throw new RedisException("비관적 락 발생");
            }

            List<String> rankingEmailList = rankingProcessService.getUserEmailsFromRanking(rankingKey);
            log.info("rankingEmailList = {}", rankingEmailList);

            List<UserInfoResponseDto> userInfoResponseDtoList = rankingEmailList
                    .stream()
                    .map(userRepository::findByEmail)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(UserInfoResponseDto::new)
                    .limit(MAX_RANKING_COUNT)
                    .toList();

            values.set(userInfoKey, objectMapper.writeValueAsString(userInfoResponseDtoList), 1, TimeUnit.DAYS);
        } catch (EntityNotFoundException e) {
            log.info("fetchUserInfo EntityNotFoundException 발생 : {}", e.getMessage());
            rankingProcessService.refreshRankingData();
            throw e;
        } finally {
            releaseLock(lockKey);
        }
    }

    private List<UserInfoResponseDto> deserializeUserResponseDtoList(String jsonString) throws IOException {
        log.info("deserializeUserResponseDtoList 실행 jsonString = {}", jsonString);
        UserInfoResponseDto[] array = objectMapper.readValue(jsonString, UserInfoResponseDto[].class);
        return Arrays.asList(array);
    }

    public boolean acquireLock(String lockKey, int timeout) {
        String value = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY_PREFIX + lockKey, value, timeout, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(success);

    }

    public void releaseLock(String lockKey) {
        String value = (String) redisTemplate.opsForValue().get(LOCK_KEY_PREFIX + lockKey);
        if (value != null) {
            // 여기서는 단순히 락 키를 삭제하여 락을 해제합니다.
            redisTemplate.delete(LOCK_KEY_PREFIX + lockKey);
        }
    }


}

