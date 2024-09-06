package com.study.dawheen.volunteer.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.QUser;
import com.study.dawheen.volunteer.dto.VolunteerUserRankingDto;
import com.study.dawheen.volunteer.entity.QUserVolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerWorkRankingRepositoryCustom;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserVolunteerWorkRankingRepositoryCustomImpl implements UserVolunteerWorkRankingRepositoryCustom {

    private static final QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;
    private static final QUser user = QUser.user;
    private final JPAQueryFactory queryFactory;

    public UserVolunteerWorkRankingRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<UserInfoResponseDto> getVolunteerActivityRankings(LocalDateTime startDateTime) {

        return queryFactory.select(Projections.constructor(UserInfoResponseDto.class, user))
                .from(userVolunteerWork)
                .join(userVolunteerWork.user, user)
                .where(userVolunteerWork.status.eq(ApplyStatus.COMPLETED)
                        .and(userVolunteerWork.modifiedDate.between(startDateTime, LocalDateTime.now())))
                .groupBy(userVolunteerWork.user)
                .orderBy(userVolunteerWork.count().desc())
                .limit(20)
                .fetch();
    }

    @Override
    public List<VolunteerUserRankingDto> getUserVolunteerCountByPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return queryFactory.select(Projections.constructor(VolunteerUserRankingDto.class, user.email, userVolunteerWork.count()))
                .from(userVolunteerWork)
                .join(userVolunteerWork.user, user)// Fetch join 사용
                .where(userVolunteerWork.status.eq(ApplyStatus.COMPLETED)
                        .and(userVolunteerWork.modifiedDate.between(startDateTime, endDateTime)))
                .groupBy(user.email) // User 엔티티의 이메일로 그룹화
                .fetch();
    }

}
