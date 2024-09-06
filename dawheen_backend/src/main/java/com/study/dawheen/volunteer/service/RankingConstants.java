package com.study.dawheen.volunteer.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class RankingConstants {

    public static final String MONTHLY_RANKING = "monthly-ranking";
    public static final String SEMI_ANNUAL_RANKING = "semi-annual-ranking";
    public static final String ANNUAL_RANKING = "annual-ranking";
    public static final String MONTHLY_RANKING_USER_INFO = "monthly-ranking-user-info";
    public static final String SEMI_ANNUAL_RANKING_USER_INFO = "semi-annual-ranking-user-info";
    public static final String ANNUAL_RANKING_USER_INFO = "annual-ranking-user-info";
    public static final int MAX_RANKING_COUNT = 20;
    public static final int BATCH_SIZE = 50;
    public static final String LOCK_KEY_PREFIX = "ranking_lock_";

}
