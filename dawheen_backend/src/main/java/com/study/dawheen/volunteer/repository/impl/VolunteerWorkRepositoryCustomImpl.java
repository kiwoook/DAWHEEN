package com.study.dawheen.volunteer.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.dawheen.common.entity.QCoordinate;
import com.study.dawheen.volunteer.dto.VolunteerInfoResponseDto;
import com.study.dawheen.volunteer.entity.QUserVolunteerWork;
import com.study.dawheen.volunteer.entity.QVolunteerWork;
import com.study.dawheen.volunteer.entity.type.ApplyStatus;
import com.study.dawheen.volunteer.entity.type.TargetAudience;
import com.study.dawheen.volunteer.entity.type.VolunteerType;
import com.study.dawheen.volunteer.repository.VolunteerWorkRepositoryCustom;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public class VolunteerWorkRepositoryCustomImpl implements VolunteerWorkRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QUserVolunteerWork userVolunteerWork = QUserVolunteerWork.userVolunteerWork;
    QVolunteerWork volunteerWork = QVolunteerWork.volunteerWork;
    QCoordinate coordinate = QCoordinate.coordinate;

    public VolunteerWorkRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public List<VolunteerInfoResponseDto> getByRadiusAndBeforeEndDate(double latitude, double longitude, double radius) {

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // 반경 필터 (반경 값을 그대로 사용)
        return queryFactory.select(Projections.fields(VolunteerInfoResponseDto.class, volunteerWork))
                .from(volunteerWork)
                .innerJoin(volunteerWork.coordinate, coordinate)
                .fetchJoin()
                .where(stDistanceSphere(latitude, longitude).loe(radius)  // 반경 값은 미터 단위로 제공해야 합니다.
                        .and(volunteerWork.recruitEndDateTime.after(now)))
                .fetch();
    }

    @Override
    public List<VolunteerInfoResponseDto> getByFiltersAndDataRangeWithinRadius(double latitude, double longitude, double radius, Set<VolunteerType> volunteerTypes, Set<TargetAudience> targetAudiences, LocalDateTime startDate, LocalDateTime endDate) {

        LocalDateTime now = LocalDateTime.now();

        BooleanExpression isTrue = Expressions.asBoolean(true).isTrue();

        return queryFactory
                .select(Projections.bean(VolunteerInfoResponseDto.class, volunteerWork))
                .from(volunteerWork)
                .join(volunteerWork.coordinate, coordinate)
                .where(stDistanceSphere(latitude, longitude).loe(radius),
                        volunteerWork.recruitEndDateTime.after(now),
                        volunteerWork.serviceStartDatetime.between(startDate, endDate),
                        volunteerWork.serviceEndDatetime.between(startDate, endDate),
                        volunteerTypes.isEmpty() ? isTrue : isContainVolunteerTypes(volunteerTypes),
                        targetAudiences.isEmpty() ? isTrue : isContainTargetAudiences(targetAudiences))
                .fetch();
    }




    private NumberTemplate<Double> stDistanceSphere(double latitude, double longitude) {
        return Expressions.numberTemplate(Double.class,
                "6371 * acos(cos(radians({0}))*cos(radians({1}))*cos(radians({2}) - radians({3})) + sin(radians({4}))*sin(radians({5})))",
                latitude,
                volunteerWork.coordinate.latitude,
                volunteerWork.coordinate.longitude,
                longitude,
                latitude,
                volunteerWork.coordinate.latitude);
    }


    private BooleanBuilder isContainVolunteerTypes(Set<VolunteerType> volunteerTypes) {

        BooleanBuilder builder = new BooleanBuilder();

        for (VolunteerType volunteerType : volunteerTypes) {
            builder.or(volunteerWork.volunteerTypes.contains(volunteerType));
        }

        return builder;
    }

    private BooleanBuilder isContainTargetAudiences(Set<TargetAudience> targetAudiences) {
        BooleanBuilder builder = new BooleanBuilder();

        for (TargetAudience targetAudience : targetAudiences) {
            builder.or(volunteerWork.targetAudiences.contains(targetAudience));
        }

        return builder;
    }


}
