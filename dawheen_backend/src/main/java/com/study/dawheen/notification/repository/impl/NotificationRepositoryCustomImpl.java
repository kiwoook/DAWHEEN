package com.study.dawheen.notification.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dawheen.notification.dto.NotificationResponseDto;
import com.study.dawheen.notification.entity.Notification;
import com.study.dawheen.notification.entity.QNotification;
import com.study.dawheen.notification.repository.NotificationRepositoryCustom;
import com.study.dawheen.user.entity.QUser;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

    private static final QNotification notification = QNotification.notification;
    private static final QUser user = QUser.user;
    private final JPAQueryFactory queryFactory;

    public NotificationRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<List<NotificationResponseDto>> findAllByReceiver(String email) {
        List<NotificationResponseDto> result = queryFactory.select(Projections.constructor(NotificationResponseDto.class, notification))
                .from(notification)
                .where(notification.receiver.eq(
                        JPAExpressions
                                .selectFrom(user)
                                .where(user.email.eq(email))
                )).fetch();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Notification> findByIdAndReceiverEmail(Long id, String email) {

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(notification)
                        .where(notification.id.eq(id)
                                .and(notification.receiver.email.eq(email)))
                        .fetchOne()
        );
    }
}
