package com.study.dawheen.organization.repository.impl;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dawheen.common.entity.QCoordinate;
import com.study.dawheen.organization.entity.Organization;
import com.study.dawheen.organization.entity.QOrganization;
import com.study.dawheen.organization.repository.OrganRepositoryCustom;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrganRepositoryCustomImpl implements OrganRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrganRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<List<Organization>> findOrganizationsWithinRadius(double latitude, double longitude, int radius) {

        QOrganization organization = QOrganization.organization;
        QCoordinate coordinate = QCoordinate.coordinate;

        NumberTemplate<Double> haversineFormula = Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0}))*cos(radians({1}))*cos(radians({2}) - radians({3})) + sin(radians({4}))*sin(radians({5})))",
                latitude, organization.coordinate.latitude, organization.coordinate.longitude, longitude, latitude, organization.coordinate.latitude);

        List<Organization> result = queryFactory
                .selectFrom(organization)
                .join(organization.coordinate, coordinate)
                .fetchJoin()
                .where(haversineFormula.loe(radius))
                .fetch();

        return Optional.ofNullable(result);


    }
}
