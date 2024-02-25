package com.study.dahween.volunteer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVolunteerWork is a Querydsl query type for VolunteerWork
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVolunteerWork extends EntityPathBase<VolunteerWork> {

    private static final long serialVersionUID = -1506482382L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVolunteerWork volunteerWork = new QVolunteerWork("volunteerWork");

    public final com.study.dahween.common.entity.QBaseTimeEntity _super = new com.study.dahween.common.entity.QBaseTimeEntity(this);

    public final SimplePath<java.util.concurrent.atomic.AtomicInteger> appliedParticipants = createSimple("appliedParticipants", java.util.concurrent.atomic.AtomicInteger.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> maxParticipants = createNumber("maxParticipants", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final com.study.dahween.organization.entity.QOrganization organization;

    public final DatePath<java.time.LocalDate> recruitEndDate = createDate("recruitEndDate", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> recruitStartDate = createDate("recruitStartDate", java.time.LocalDate.class);

    public final SetPath<java.time.DayOfWeek, EnumPath<java.time.DayOfWeek>> serviceDays = this.<java.time.DayOfWeek, EnumPath<java.time.DayOfWeek>>createSet("serviceDays", java.time.DayOfWeek.class, EnumPath.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> serviceEndDate = createDate("serviceEndDate", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> serviceEndTime = createTime("serviceEndTime", java.time.LocalTime.class);

    public final DatePath<java.time.LocalDate> serviceStartDate = createDate("serviceStartDate", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> serviceStartTime = createTime("serviceStartTime", java.time.LocalTime.class);

    public final SetPath<com.study.dahween.volunteer.entity.type.TargetAudience, EnumPath<com.study.dahween.volunteer.entity.type.TargetAudience>> targetAudiences = this.<com.study.dahween.volunteer.entity.type.TargetAudience, EnumPath<com.study.dahween.volunteer.entity.type.TargetAudience>>createSet("targetAudiences", com.study.dahween.volunteer.entity.type.TargetAudience.class, EnumPath.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final SetPath<UserVolunteerWork, QUserVolunteerWork> users = this.<UserVolunteerWork, QUserVolunteerWork>createSet("users", UserVolunteerWork.class, QUserVolunteerWork.class, PathInits.DIRECT2);

    public final SetPath<com.study.dahween.volunteer.entity.type.VolunteerType, EnumPath<com.study.dahween.volunteer.entity.type.VolunteerType>> volunteerTypes = this.<com.study.dahween.volunteer.entity.type.VolunteerType, EnumPath<com.study.dahween.volunteer.entity.type.VolunteerType>>createSet("volunteerTypes", com.study.dahween.volunteer.entity.type.VolunteerType.class, EnumPath.class, PathInits.DIRECT2);

    public QVolunteerWork(String variable) {
        this(VolunteerWork.class, forVariable(variable), INITS);
    }

    public QVolunteerWork(Path<? extends VolunteerWork> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVolunteerWork(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVolunteerWork(PathMetadata metadata, PathInits inits) {
        this(VolunteerWork.class, metadata, inits);
    }

    public QVolunteerWork(Class<? extends VolunteerWork> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.organization = inits.isInitialized("organization") ? new com.study.dahween.organization.entity.QOrganization(forProperty("organization"), inits.get("organization")) : null;
    }

}

