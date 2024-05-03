package com.study.dawheen.organization.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dawheen.organization.entity.QOrganizationSubscribe;
import com.study.dawheen.organization.repository.OrganSubscribeRepositoryCustom;
import com.study.dawheen.user.dto.UserInfoResponseDto;
import com.study.dawheen.user.entity.QUser;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrganSubscribeRepositoryCustomImpl implements OrganSubscribeRepositoryCustom {

    private static final QUser USER = QUser.user;
    private static final QOrganizationSubscribe ORGANIZATION_SUBSCRIBE = QOrganizationSubscribe.organizationSubscribe;
    private final JPAQueryFactory queryFactory;

    public OrganSubscribeRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<UserInfoResponseDto> findUserByOrganizationId(Long id) {
        return queryFactory.select(Projections.constructor(UserInfoResponseDto.class, ORGANIZATION_SUBSCRIBE.user))
                .from(ORGANIZATION_SUBSCRIBE)
                .join(ORGANIZATION_SUBSCRIBE.user, USER)
                .where(ORGANIZATION_SUBSCRIBE.organization.id.eq(id))
                .fetch();

    }
}
