package com.study.dawheen.volunteer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolunteerRankingService {

    private static final String MONTHLY_RANKING = "monthly-ranking";
    private static final String SEMI_ANNUAL_RANKING = "semi-annual-ranking";
    private static final String ANNUAL_RANKING = "annual-ranking";
    private final UserVolunteerRepository userVolunteerRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // @Scheduled(cron = "0 0 0 1 * ?") 매달 1일에만 가중치 계산

    @Scheduled(cron = "0 0 0 1 * ?")
    public void fetchMonthlyRankingToRedis() throws JsonProcessingException {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        List<UserInfoResponseDto> responseDtoList = userVolunteerRepository.getMonthlyVolunteerActivityRankings();

        if (responseDtoList.isEmpty()) {
            return;
        }

        values.set(MONTHLY_RANKING, objectMapper.writeValueAsString(responseDtoList), 1, TimeUnit.DAYS);
        log.info("월간 봉사활동 랭킹 실행 완료");

    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void fetchSemiAnnualRankingToRedis() {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        List<UserInfoResponseDto> responseDtoList = userVolunteerRepository.getSemiAnnualVolunteerActivityRankings();

        if (responseDtoList.isEmpty()) {
            return;
        }

        values.set(SEMI_ANNUAL_RANKING, responseDtoList, 1, TimeUnit.DAYS);
        log.info("6개월간 봉사활동 랭킹 실행 완료");


    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void fetchAnnualRankingToRedis() {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        List<UserInfoResponseDto> responseDtoList = userVolunteerRepository.getYearlyVolunteerActivityRankings();

        if (responseDtoList.isEmpty()) {
            return;
        }
        values.set(ANNUAL_RANKING, responseDtoList, 1, TimeUnit.DAYS);
        log.info("연간 봉사활동 랭킹 실행 완료");

    }

    @Transactional(readOnly = true)
    public List<UserInfoResponseDto> getMonthlyRanking() throws IOException, EntityNotFoundException {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();

        if (values.get(MONTHLY_RANKING) == null) {
            throw new EntityNotFoundException();
        }
        return deserializeUserResponseDtoList((String) values.get(MONTHLY_RANKING));
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponseDto> getSemiAnnualRanking() throws IOException, EntityNotFoundException {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();

        if (values.get(SEMI_ANNUAL_RANKING) == null) {
            throw new EntityNotFoundException();
        }
        return deserializeUserResponseDtoList((String) values.get(SEMI_ANNUAL_RANKING));
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponseDto> getAnnualRanking() throws IOException, EntityNotFoundException {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();

        if (values.get(ANNUAL_RANKING) == null) {
            throw new EntityNotFoundException();
        }

        return deserializeUserResponseDtoList((String) values.get(ANNUAL_RANKING));

    }

    private List<UserInfoResponseDto> deserializeUserResponseDtoList(String jsonString) throws IOException {
        UserInfoResponseDto[] array = objectMapper.readValue(jsonString, UserInfoResponseDto[].class);
        return Arrays.asList(array);
    }

}
