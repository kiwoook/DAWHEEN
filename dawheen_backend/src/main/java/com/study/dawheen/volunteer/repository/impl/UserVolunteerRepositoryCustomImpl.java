package com.study.dawheen.volunteer.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dawheen.user.entity.QUser;
import com.study.dawheen.user.entity.User;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.QUserVolunteerWork;
import com.study.dawheen.volunteer.entity.QVolunteerWork;
import com.study.dawheen.volunteer.entity.UserVolunteerWork;
import com.study.dawheen.volunteer.entity.VolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.repository.UserVolunteerRepositoryCustom;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserVolunteerRepositoryCustomImpl implements UserVolunteerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;
    private static final QVolunteerWork volunteerWork = QVolunteerWork.volunteerWork;
    private static final QUser user = QUser.user;

    public UserVolunteerRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public boolean existsByVolunteerWorkAndUserIdAndStatus(Long volunteerWorkId, Long userId, List<ApplyStatus> statuses) {

        // Exist 관련 조회는 First 로 한 다음에 Null Check 를 하는게 성능 개선에 도움이 된다.

        return queryFactory.selectFrom(userVolunteerWork)
                .where(userVolunteerWork.user.id.eq(userId)
                        .and(userVolunteerWork.volunteerWork.id.eq(volunteerWorkId))
                        .and(userVolunteerWork.status.in(statuses)))
                .fetchFirst() != null;
    }

    @Override
    public boolean existsByVolunteerWorkAndEmailAndStatus(Long volunteerWorkId, String email, List<ApplyStatus> statuses) {

        // Exist 관련 조회는 First 로 한 다음에 Null Check 를 하는게 성능 개선에 도움이 된다.

        return queryFactory.selectFrom(userVolunteerWork)
                .where(userVolunteerWork.user.email.eq(email)
                        .and(userVolunteerWork.volunteerWork.id.eq(volunteerWorkId))
                        .and(userVolunteerWork.status.in(statuses)))
                .fetchFirst() != null;
    }


    @Override
    public List<UserVolunteerWork> findAllByVolunteerWorkIdWithFetch(Long volunteerWorkId) {

        return queryFactory
                .selectFrom(userVolunteerWork)
                .join(userVolunteerWork.user, user).fetchJoin()
                .where(
                        userVolunteerWork.volunteerWork.id.eq(volunteerWorkId)
                ).fetch();

    }

    @Override
    public List<User> findUsersByVolunteerWorkIdAndStatus(Long volunteerWorkId, ApplyStatus status) {

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
    public List<User> findUsersByVolunteerWorkId(Long volunteerWorkId) {

        return queryFactory
                .select(userVolunteerWork.user)
                .distinct()
                .from(userVolunteerWork)
                .join(userVolunteerWork.user, user).fetchJoin()
                .where(userVolunteerWork.volunteerWork.id.eq(volunteerWorkId))
                .fetch();
    }


    @Override
    public Optional<UserVolunteerWork> findByVolunteerWorkIdAndUserId(Long volunteerWorkId, Long userId) {

        return Optional.of(Objects.requireNonNull(
                queryFactory
                        .selectFrom(userVolunteerWork)
                        .join(userVolunteerWork.volunteerWork, volunteerWork)
                        .join(userVolunteerWork.user, user).fetchJoin()
                        .where(
                                userVolunteerWork.volunteerWork.id.eq(volunteerWorkId)
                                        .and(userVolunteerWork.user.id.eq(userId))
                        ).fetchOne()));
    }


    @Override
    public Optional<UserVolunteerWork> findByVolunteerWorkIdAndEmail(Long volunteerWorkId, String email) {

        return Optional.of(Objects.requireNonNull(
                queryFactory
                        .selectFrom(userVolunteerWork)
                        .join(userVolunteerWork.volunteerWork, volunteerWork)
                        .join(userVolunteerWork.user, user).fetchJoin()
                        .where(
                                userVolunteerWork.volunteerWork.id.eq(volunteerWorkId)
                                        .and(userVolunteerWork.user.email.eq(email))
                        ).fetchOne()));
    }

    @Override
    public Page<VolunteerInfoResponseDto> findVolunteerWorkByEmailAndStatus(String email, ApplyStatus status, Pageable pageable) {

        List<VolunteerInfoResponseDto> content = queryFactory.select(
                        Projections.constructor(VolunteerInfoResponseDto.class, volunteerWork))
                .from(volunteerWork).where(
                        volunteerWork.eq(
                                JPAExpressions.select(userVolunteerWork.volunteerWork).from(userVolunteerWork)
                                        .where(
                                                userVolunteerWork.status.eq(status)
                                                        .and(
                                                                userVolunteerWork.user.email.eq(email)
                                                        )
                                        )

                        )
                ).offset(pageable.getOffset() - 1)
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<VolunteerWork> count = queryFactory
                .selectFrom(volunteerWork).where(
                        userVolunteerWork.status.eq(status)
                                .and(
                                        userVolunteerWork.user.email.eq(email)
                                )
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchCount);
    }
}
