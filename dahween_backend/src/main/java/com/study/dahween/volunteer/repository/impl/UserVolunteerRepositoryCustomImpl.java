package com.study.dahween.volunteer.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dahween.user.entity.QUser;
import com.study.dahween.user.entity.User;
import com.study.dahween.volunteer.entity.QUserVolunteerWork;
import com.study.dahween.volunteer.entity.QVolunteerWork;
import com.study.dahween.volunteer.entity.UserVolunteerWork;
import com.study.dahween.volunteer.entity.type.ApplyStatus;
import com.study.dahween.volunteer.repository.UserVolunteerRepositoryCustom;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserVolunteerRepositoryCustomImpl implements UserVolunteerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserVolunteerRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public boolean existsByVolunteerWorkAndUserAndStatus(Long volunteerWorkId, String userId, List<ApplyStatus> statuses) {
        QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;

        // Exist 관련 조회는 First 로 한 다음에 Null Check 를 하는게 성능 개선에 도움이 된다.

        return queryFactory.selectFrom(userVolunteerWork)
                .where(userVolunteerWork.user.userId.eq(userId)
                        .and(userVolunteerWork.volunteerWork.id.eq(volunteerWorkId))
                        .and(userVolunteerWork.status.in(statuses)))
                .fetchFirst() != null;

    }

    @Override
    public Optional<List<UserVolunteerWork>> findAllByVolunteerWorkIdWithFetch(Long volunteerWorkId) {
        QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;
        QUser user = QUser.user;

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(userVolunteerWork)
                        .join(userVolunteerWork.user, user).fetchJoin()
                        .where(
                                userVolunteerWork.volunteerWork.id.eq(volunteerWorkId)
                        ).fetch()
        );
    }

    @Override
    public List<User> findUsersByVolunteerWorkIdAndStatus(Long volunteerWorkId, ApplyStatus status) {
        QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;
        QUser user = QUser.user;
        return queryFactory
                .select(userVolunteerWork.user)
                .distinct()
                .from(userVolunteerWork)
                .join(userVolunteerWork.user, user).fetchJoin()
                .where(
                        userVolunteerWork.volunteerWork.id.eq(volunteerWorkId)
                                .and(userVolunteerWork.status.eq(status))
                )
                .fetch();
    }

    @Override
    public Optional<List<User>> findUsersByVolunteerWorkId(Long volunteerWorkId) {
        QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;
        QUser user = QUser.user;
        return Optional.ofNullable(queryFactory
                .select(userVolunteerWork.user)
                .distinct()
                .from(userVolunteerWork)
                .join(userVolunteerWork.user, user).fetchJoin()
                .where(userVolunteerWork.volunteerWork.id.eq(volunteerWorkId))
                .fetch()
        );
    }


    @Override
    public Optional<UserVolunteerWork> findByVolunteerWorkIdAndUserId(Long volunteerWorkId, String userId) {
        QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;
        QVolunteerWork volunteerWork = QVolunteerWork.volunteerWork;
        QUser user = QUser.user;
        return Optional.ofNullable(queryFactory
                .selectFrom(userVolunteerWork)
                .join(userVolunteerWork.volunteerWork, volunteerWork)
                .join(userVolunteerWork.user, user).fetchJoin()
                .where(
                        userVolunteerWork.volunteerWork.id.eq(volunteerWorkId)
                                .and(userVolunteerWork.user.userId.eq(userId))
                ).fetchOne());
    }

    @Override
    public int countAllByVolunteerWorkIdAndStatus(Long volunteerWorkId, ApplyStatus status) {
        QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;
        QVolunteerWork volunteerWork = QVolunteerWork.volunteerWork;

        return queryFactory
                .selectFrom(userVolunteerWork)
                .join(userVolunteerWork.volunteerWork, volunteerWork).fetchJoin()
                .where(
                        userVolunteerWork.volunteerWork.id.eq(volunteerWorkId)
                                .and(userVolunteerWork.status.eq(status))
                )
                .fetch().size();
    }


}
