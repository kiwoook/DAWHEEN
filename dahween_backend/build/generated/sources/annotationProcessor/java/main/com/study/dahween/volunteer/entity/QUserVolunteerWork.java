package com.study.dahween.volunteer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserVolunteerWork is a Querydsl query type for UserVolunteerWork
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserVolunteerWork extends EntityPathBase<UserVolunteerWork> {

    private static final long serialVersionUID = 1645500839L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserVolunteerWork userVolunteerWork = new QUserVolunteerWork("userVolunteerWork");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.study.dahween.volunteer.entity.type.ApplyStatus> status = createEnum("status", com.study.dahween.volunteer.entity.type.ApplyStatus.class);

    public final com.study.dahween.user.entity.QUser user;

    public final QVolunteerWork volunteerWork;

    public QUserVolunteerWork(String variable) {
        this(UserVolunteerWork.class, forVariable(variable), INITS);
    }

    public QUserVolunteerWork(Path<? extends UserVolunteerWork> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserVolunteerWork(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserVolunteerWork(PathMetadata metadata, PathInits inits) {
        this(UserVolunteerWork.class, metadata, inits);
    }

    public QUserVolunteerWork(Class<? extends UserVolunteerWork> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.study.dahween.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
        this.volunteerWork = inits.isInitialized("volunteerWork") ? new QVolunteerWork(forProperty("volunteerWork"), inits.get("volunteerWork")) : null;
    }

}

