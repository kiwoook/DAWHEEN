package com.study.dawheen.volunteer.service;

import java.util.List;

public interface RankingProcessService {

    void incrementRankingScores(String email);

    void refreshRankingData();

    List<String> getUserEmailsFromRanking(String rankingKey);

}
