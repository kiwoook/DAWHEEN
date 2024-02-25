package com.study.dahween.organization.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrganization is a Querydsl query type for Organization
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrganization extends EntityPathBase<Organization> {

    private static final long serialVersionUID = 2054195521L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrganization organization = new QOrganization("organization");

    public final com.study.dahween.common.entity.QBaseTimeEntity _super = new com.study.dahween.common.entity.QBaseTimeEntity(this);

    public final com.study.dahween.common.entity.QAddress address;

    public final BooleanPath approved = createBoolean("approved");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final StringPath facilityPhone = createString("facilityPhone");

    public final StringPath facilityType = createString("facilityType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final StringPath representName = createString("representName");

    public final ListPath<com.study.dahween.user.entity.User, com.study.dahween.user.entity.QUser> users = this.<com.study.dahween.user.entity.User, com.study.dahween.user.entity.QUser>createList("users", com.study.dahween.user.entity.User.class, com.study.dahween.user.entity.QUser.class, PathInits.DIRECT2);

    public final ListPath<com.study.dahween.volunteer.entity.VolunteerWork, com.study.dahween.volunteer.entity.QVolunteerWork> workList = this.<com.study.dahween.volunteer.entity.VolunteerWork, com.study.dahween.volunteer.entity.QVolunteerWork>createList("workList", com.study.dahween.volunteer.entity.VolunteerWork.class, com.study.dahween.volunteer.entity.QVolunteerWork.class, PathInits.DIRECT2);

    public QOrganization(String variable) {
        this(Organization.class, forVariable(variable), INITS);
    }

    public QOrganization(Path<? extends Organization> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrganization(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrganization(PathMetadata metadata, PathInits inits) {
        this(Organization.class, metadata, inits);
    }

    public QOrganization(Class<? extends Organization> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.study.dahween.common.entity.QAddress(forProperty("address")) : null;
    }

}

