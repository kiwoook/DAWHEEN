package com.study.dawheen.volunteer.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.QUser;
import com.study.dawheen.volunteer.entity.QUserVolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerWorkRankingRepositoryCustom;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserVolunteerWorkRankingRepositoryCustomImpl implements UserVolunteerWorkRankingRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;
    private static final QUser user = QUser.user;

    public UserVolunteerWorkRankingRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<UserInfoResponseDto> getMonthlyVolunteerActivityRankings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(1);
        return queryFactory.select(Projections.constructor(UserInfoResponseDto.class, user))
                .from(userVolunteerWork)
                .join(user, userVolunteerWork.user)
                .fetchJoin()
                .where(userVolunteerWork.status.eq(ApplyStatus.COMPLETED)
                        .and(userVolunteerWork.modifiedDate.between(startDate, LocalDateTime.now())))
                .groupBy(userVolunteerWork.user)
                .orderBy(userVolunteerWork.count().desc())
                .limit(20)
                .fetch();
    }

    @Override
    public List<UserInfoResponseDto> getSemiAnnualVolunteerActivityRankings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(6);
        return queryFactory.select(Projections.constructor(UserInfoResponseDto.class, user))
                .from(userVolunteerWork)
                .join(user, userVolunteerWork.user)
                .fetchJoin()
                .where(userVolunteerWork.status.eq(ApplyStatus.COMPLETED)
                        .and(userVolunteerWork.modifiedDate.between(startDate, LocalDateTime.now())))
                .groupBy(userVolunteerWork.user)
                .orderBy(userVolunteerWork.count().desc())
                .limit(20)
                .fetch();
    }

    @Override
    public List<UserInfoResponseDto> getYearlyVolunteerActivityRankings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusYears(1);
        return queryFactory.select(Projections.constructor(UserInfoResponseDto.class, user))
                .from(userVolunteerWork)
                .join(user, userVolunteerWork.user)
                .fetchJoin()
                .where(userVolunteerWork.status.eq(ApplyStatus.COMPLETED)
                        .and(userVolunteerWork.modifiedDate.between(startDate, LocalDateTime.now())))
                .groupBy(userVolunteerWork.user)
                .orderBy(userVolunteerWork.count().desc())
                .limit(20)
                .fetch();
    }
}
