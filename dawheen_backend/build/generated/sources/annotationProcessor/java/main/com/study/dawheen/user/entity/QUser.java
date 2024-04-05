package com.study.dawheen.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 791722803L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final com.study.dawheen.common.entity.QBaseTimeEntity _super = new com.study.dawheen.common.entity.QBaseTimeEntity(this);

    public final com.study.dawheen.common.entity.QAddress address;

    public final StringPath birth = createString("birth");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedDate = _super.modifiedDate;

    public final StringPath name = createString("name");

    public final com.study.dawheen.organization.entity.QOrganization organization;

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final EnumPath<com.study.dawheen.auth.entity.ProviderType> providerType = createEnum("providerType", com.study.dawheen.auth.entity.ProviderType.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final EnumPath<RoleType> role = createEnum("role", RoleType.class);

    public final StringPath socialId = createString("socialId");

    public final SetPath<com.study.dawheen.volunteer.entity.UserVolunteerWork, com.study.dawheen.volunteer.entity.QUserVolunteerWork> volunteerWorks = this.<com.study.dawheen.volunteer.entity.UserVolunteerWork, com.study.dawheen.volunteer.entity.QUserVolunteerWork>createSet("volunteerWorks", com.study.dawheen.volunteer.entity.UserVolunteerWork.class, com.study.dawheen.volunteer.entity.QUserVolunteerWork.class, PathInits.DIRECT2);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.study.dawheen.common.entity.QAddress(forProperty("address")) : null;
        this.organization = inits.isInitialized("organization") ? new com.study.dawheen.organization.entity.QOrganization(forProperty("organization"), inits.get("organization")) : null;
    }

}
