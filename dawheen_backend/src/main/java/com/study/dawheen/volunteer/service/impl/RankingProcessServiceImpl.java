package com.study.dawheen.volunteer.service.impl;

import com.study.dawheen.volunteer.dto.VolunteerUserRankingDto;
import com.study.dawheen.volunteer.repository.UserVolunteerRepository;
import com.study.dawheen.volunteer.service.RankingProcessService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.study.dawheen.volunteer.service.RankingConstants.*;

@Service
@Slf4j
public class RankingProcessServiceImpl implements RankingProcessService {

    private final ZSetOperations<String, Object> zSetOperations;
    private final UserVolunteerRepository userVolunteerRepository;


    public RankingProcessServiceImpl(RedisTemplate<String, Object> redisTemplate, UserVolunteerRepository userVolunteerRepository) {
        this.zSetOperations = redisTemplate.opsForZSet();
        this.userVolunteerRepository = userVolunteerRepository;
    }

    @Override
    public void incrementRankingScores(String email) {
        log.info("incrementRankingScores 실행");
        zSetOperations.incrementScore(MONTHLY_RANKING, email, 1);
        zSetOperations.incrementScore(SEMI_ANNUAL_RANKING, email, 1);
        zSetOperations.incrementScore(ANNUAL_RANKING, email, 1);
    }

    @Override
    public void refreshRankingData() {
        log.info("refreshRankingData 실행");

        List<VolunteerUserRankingDto> monthlyRankingList = userVolunteerRepository.getUserVolunteerCountByPeriod(
                LocalDateTime.now().minusMonths(1), LocalDateTime.now());

        monthlyRankingList.forEach(rankingDto -> {
            zSetOperations.add(MONTHLY_RANKING, rankingDto.getUserEmail(), rankingDto.getCount());
            zSetOperations.add(SEMI_ANNUAL_RANKING, rankingDto.getUserEmail(), rankingDto.getCount());
            zSetOperations.add(ANNUAL_RANKING, rankingDto.getUserEmail(), rankingDto.getCount());
        });

        // 2. 6달전~1달전 데이터 가져오기
        List<VolunteerUserRankingDto> semiAnnualRankingList = userVolunteerRepository.getUserVolunteerCountByPeriod(
                LocalDateTime.now().minusMonths(6), LocalDateTime.now().minusMonths(1));

        semiAnnualRankingList.forEach(rankingDto -> {
            zSetOperations.incrementScore(SEMI_ANNUAL_RANKING, rankingDto.getUserEmail(), rankingDto.getCount());
            zSetOperations.incrementScore(ANNUAL_RANKING, rankingDto.getUserEmail(), rankingDto.getCount());
        });

        // 3. 1년전~6달전 데이터 가져오기
        List<VolunteerUserRankingDto> annualRankingList = userVolunteerRepository.getUserVolunteerCountByPeriod(
                LocalDateTime.now().minusYears(1), LocalDateTime.now().minusMonths(6));

        annualRankingList.forEach(rankingDto ->
                zSetOperations.incrementScore(ANNUAL_RANKING, rankingDto.getUserEmail(), rankingDto.getCount())
        );

        if (monthlyRankingList.isEmpty() && semiAnnualRankingList.isEmpty() && annualRankingList.isEmpty()) {
            throw new EntityNotFoundException();
        }

        // 각 랭킹 key 를 초기화시켜준다.
        zSetOperations.getOperations().expire(MONTHLY_RANKING_USER_INFO, 0, TimeUnit.SECONDS);
        zSetOperations.getOperations().expire(SEMI_ANNUAL_RANKING_USER_INFO, 0, TimeUnit.SECONDS);
        zSetOperations.getOperations().expire(ANNUAL_RANKING_USER_INFO, 0, TimeUnit.SECONDS);

    }

    @Override
    public List<String> getUserEmailsFromRanking(String rankingKey) throws EntityNotFoundException {

        log.info("getUserEmailsFromRanking 실행 rankingKey = {}", rankingKey);

        Set<ZSetOperations.TypedTuple<Object>> typedTuples = zSetOperations.reverseRangeWithScores(rankingKey, 0, MAX_RANKING_COUNT - 1);

        if (typedTuples == null || typedTuples.isEmpty()) {
            log.error("typedTuple is Empty");

            throw new EntityNotFoundException();
        }

        List<Map.Entry<String, Double>> entries = typedTuples.stream()
                .map(tuple -> new AbstractMap.SimpleEntry<>(Objects.requireNonNull(tuple.getValue()).toString(), tuple.getScore())) // key와 score로 변환
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());


        return entries.stream()
                .map(Map.Entry::getKey)
                .toList();
    }
}
